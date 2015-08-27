package siani.synchronizer;

import siani.synchronizer.SynchronizerFile.Line;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class Synchronizer {

	private final SynchronizerFile parent;
	private final SynchronizerFile child;

	public Synchronizer(File parent, File child) {
		this.parent = new SynchronizerFile(parent);
		this.child = new SynchronizerFile(child);
	}

	public void sync() {
		int blockSize;
		for (blockSize = parent.linesSize(); blockSize >= 1; blockSize--)
			matchBlocksInChild(parent.split(blockSize)).stream().
				filter(child::remove).
				forEach(parent::remove);
		writeSyncedFile(propagateChanges());
	}

	private List<Block> matchBlocksInChild(List<Block> blocks) {
		return blocks.stream().filter(child::contains).collect(toList());
	}

	private List<String> propagateChanges() {
		List<String> newLines = new ArrayList<>(child.lines());
		List<List<Line>> changes = packConsecutiveChanges(parent.remainingLines());
		for (int i = changes.size() - 1; i >= 0; i--) {
			final List<Line> changedLines = changes.get(i);
			if (isEmptyContent(changedLines)) continue;
			int position = calculatePositionOf(removedBlockOfNextLine(changedLines.get(changedLines.size() - 1)));
			newLines.add(position++, "// MERGE");
			newLines.addAll(position, changedLines.stream().map(line -> "//" + line.content()).collect(toList()));
		}
		return newLines;
	}

	private boolean isEmptyContent(List<Line> lines) {
		for (Line line : lines)
			if (!line.content().trim().isEmpty()) return false;
		return true;
	}

	private List<List<Line>> packConsecutiveChanges(List<Line> lines) {
		List<List<Line>> linesPacked = new ArrayList<>();
		List<Line> temp = new ArrayList<>();
		temp.add(lines.get(0));
		for (int i = 0; i < lines.size() - 1; i++)
			if (areConsecutive(lines.get(i + 1), lines.get(i))) {
				temp.add(lines.get(i + 1));
			} else {
				linesPacked.add(temp);
				temp = new ArrayList<>();
				temp.add(lines.get(i + 1));
			}
		linesPacked.add(temp);
		return linesPacked;
	}

	private boolean areConsecutive(Line a, Line b) {
		return a.number() == b.number() + 1;
	}

	private int calculatePositionOf(Block block) {
		return block != null ?
			block.getLines().get(0).number() :
			child.linesSize();
	}

	private Block removedBlockOfNextLine(Line line) {
		for (Map.Entry<Block, Block> entry : child.removedBlocks().entrySet())
			if (isNextLine(line, entry.getKey().getLines().get(0))) return entry.getValue();
		return null;
	}

	private boolean isNextLine(Line currentLine, Line next) {
		return next.number() == currentLine.number() + 1;
	}

	private void writeSyncedFile(List<String> lines) {
		try {
			final FileWriter writer = new FileWriter(new File(child.file().getParent(), "__" + child.file().getName()));
			for (String line : lines) writer.write(line + SynchronizerFile.NEW_LINE);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
