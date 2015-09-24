package siani.lasso;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Lasso {

	private final LassoFile parent;
	private final LassoFile child;
	private final boolean override;

	public Lasso(File parent, File child) {
		this(parent, child, true);
	}

	public Lasso(File parent, File child, boolean override) {
		this.parent = new LassoFile(parent);
		this.child = new LassoFile(child);
		this.override = override;
	}

	public void execute() {
		if (!parent.file().exists() || !child.file().exists()) return;
		int blockSize;
		for (blockSize = parent.linesSize(); blockSize >= 1; blockSize--) {
			List<Block> matches = matchBlocksInChild(parent.split(blockSize));
			while (!matches.isEmpty()) {
				for (Block match : matches)
					if (child.remove(match))
						parent.remove(match);
				matches = matchBlocksInChild(parent.split(blockSize));
			}
		}
		writeSyncedFile(propagateChanges());
	}

	private List<Block> matchBlocksInChild(List<Block> blocks) {
		if (blocks.isEmpty()) return Collections.emptyList();
		return isInChild(blocks);
	}

	private List<Block> isInChild(List<Block> blocks) {
		List<Block> list = new ArrayList<>();
		for (Block block : blocks) if (child.contains(block)) list.add(block);
		return list;
	}

	private List<List<LassoFile.Line>> packConsecutiveChanges(List<LassoFile.Line> lines) {
		List<List<LassoFile.Line>> linesPacked = new ArrayList<>();
		List<LassoFile.Line> temp = new ArrayList<>();
		temp.add(lines.get(0));
		for (int i = 0; i < lines.size() - 1; i++)
			if (areConsecutive(lines.get(i + 1), lines.get(i)))
				temp.add(lines.get(i + 1));
			else {
				linesPacked.add(temp);
				temp = new ArrayList<>();
				temp.add(lines.get(i + 1));
			}
		linesPacked.add(temp);
		return linesPacked;
	}

	private List<String> propagateChanges() {
		List<String> newLines = new ArrayList<>(child.lines());
		if (parent.remainingLines().isEmpty()) return newLines;
		List<List<LassoFile.Line>> changes = packConsecutiveChanges(parent.remainingLines());
		for (int i = changes.size() - 1; i >= 0; i--) {
			final List<LassoFile.Line> changedLines = changes.get(i);
			if (isEmptyContent(changedLines)) continue;
			int position = calculatePositionOf(blockOfPreviousLine(changedLines.get(0)));
			newLines.add(position++, LassoFile.COMMENT_PREFIX + "MERGE");
			newLines.addAll(position, addComment(changedLines));
		}
		return newLines;
	}

	private List<String> addComment(List<LassoFile.Line> changedLines) {
		List<String> commentedLines = new ArrayList<>();
		for (LassoFile.Line line : changedLines) commentedLines.add(LassoFile.COMMENT_PREFIX + line.content());
		return commentedLines;
	}

	private boolean isEmptyContent(List<LassoFile.Line> lines) {
		for (LassoFile.Line line : lines)
			if (!line.content().trim().isEmpty()) return false;
		return true;
	}

	private boolean areConsecutive(LassoFile.Line a, LassoFile.Line b) {
		return a.number() == b.number() + 1;
	}

	private List<Block> packConsecutiveBlocks(List<Block> blocks) {
		List<Block> packedBlocks = new ArrayList<>();
		Block temp = blocks.get(0);
		for (int i = 0; i < blocks.size() - 1; i++)
			if (areConsecutive(blocks.get(i + 1), blocks.get(i)))
				temp.lines().addAll(blocks.get(i + 1).lines());
			else {
				packedBlocks.add(temp);
				temp = blocks.get(i + 1);
			}
		packedBlocks.add(temp);
		return packedBlocks;
	}

	private boolean areConsecutive(Block a, Block b) {
		return a.lines().get(0).number() == b.lines().get(b.size() - 1).number() + 1;
	}

	private int calculatePositionOf(Block block) {
		return block != null ?
			block.lines().get(block.size() - 1).number() + 1 : 0;
	}

	private Block blockOfPreviousLine(LassoFile.Line line) {
		for (Map.Entry<Block, Block> entry : child.removedBlocks().entrySet())
			if (isPreviousLine(line, entry.getKey().lines().get(entry.getKey().size() - 1))) return entry.getValue();
		return null;
	}

	private boolean isPreviousLine(LassoFile.Line currentLine, LassoFile.Line next) {
		return next.number() == currentLine.number() - 1;
	}

	private void writeSyncedFile(List<String> lines) {
		try {
			final FileWriter writer = new FileWriter(getDestiny());
			for (String line : lines) writer.write(line + LassoFile.NEW_LINE);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getDestiny() {
		return override ? child.file() : new File(child.file().getParent(), "__" + child.file().getName());
	}
}
