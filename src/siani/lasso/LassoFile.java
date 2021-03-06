package siani.lasso;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

class LassoFile {
	public static String NEW_LINE = "\n";
	private File file;
	private final LassoComment lassoComment;
	private List<String> lines = new ArrayList<>();
	private List<Line> remainingLines = new ArrayList<>();
	private Map<Block, Block> removedBlocks = new LinkedHashMap<>();

	public LassoFile(File file, LassoComment lassoComment, boolean skipIndent) {
		this.file = file;
		this.lassoComment = lassoComment;
		lines = readLinesOfFile(file);
		remainingLines = buildRemainingLines(lines, skipIndent);
	}

	private List<Line> buildRemainingLines(List<String> lines, boolean skipIndent) {
		List<Line> rLines = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++)
			rLines.add(new Line(i, uncomment(lines.get(i)), skipIndent));
		return rLines;
	}

	private String uncomment(String line) {
		if (line.trim().startsWith(lassoComment.begin()))
			line = line.replaceFirst(lassoComment.begin(), "");
		if (line.trim().endsWith(lassoComment.end()))
			line = line.substring(0, line.length() - lassoComment.end().length());
		return line;
	}

	public File file() {
		return file;
	}

	public List<String> lines() {
		return Collections.unmodifiableList(lines);
	}

	public List<Line> remainingLines() {
		return remainingLines;
	}

	public Map<Block, Block> removedBlocks() {
		return removedBlocks;
	}

	public List<Block> split(int size) {
		List<Block> blocks = new ArrayList<>();
		int i;
		for (i = 0; i + size <= remainingLines.size(); i = i + 1) {
			final List<Line> lines = remainingLines.subList(i, i + size);
			if (areConsecutive(lines) && lines.size() == size) blocks.add(new Block(clone(lines)));
		}
		return blocks;
	}

	private List<Line> clone(List<Line> lines) {
		List<Line> clonedList = new ArrayList<>();
		for (Line line : lines)
			try {
				clonedList.add((Line) line.clone());
			} catch (CloneNotSupportedException ignored) {
			}
		return clonedList;
	}

	private boolean areConsecutive(List<Line> lines) {
		if (lines.isEmpty()) return false;
		for (int i = 0; i < lines.size() - 1; i++)
			if (lines.get(i + 1).number() != lines.get(i).number() + 1) return false;
		return true;
	}

	public int linesSize() {
		return lines.size();
	}

	private List<String> readLinesOfFile(File file) {
		try {
			return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		} catch (IOException ignored) {
		}
		return Collections.emptyList();
	}

	public boolean remove(Block block) {
		if (remainingLines.isEmpty() || block == null) return false;
		int index = indexOf(block);
		if (index < 0) return false;
		final Block blockInside = findBlockInside(block);
		removedBlocks.put(getClone(block), blockInside);
		remainingLines().subList(index, index + blockInside.size()).clear();
		return true;
	}

	private Block getClone(Block block) {
		try {
			return (Block) block.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean contains(Block block) {
		return indexOf(block) >= 0;
	}

	public Block findBlockInside(Block block) {
		final int i = indexOf(block);
		return i < 0 ? null : new Block(clone(remainingLines.subList(i, i + block.size())));
	}

	public int indexOf(Block block) {
		if (remainingLines.size() < block.size()) return -1;
		int i = 0;
		while (i >= 0) {
			int index = remainingLines.subList(i, remainingLines.size()).indexOf(block.lines().get(0));
			if (index == -1) return -1;
			i = index + i;
			if (fits(block, i) && checkCompleteBlock(block, i)) return i;
			i++;
		}
		return -1;
	}

	private boolean fits(Block block, int i) {
		return !(i < 0 || i + block.size() > remainingLines.size());
	}

	private boolean checkCompleteBlock(Block block, int firstLine) {
		for (int i = firstLine; i < firstLine + block.size(); i++)
			if (!remainingLines.get(i).equals(block.lines().get(i - firstLine))) return false;
		return true;
	}

	@Override
	public String toString() {
		return file.getPath();
	}


	public static class Line implements Cloneable {

		private int number;
		private String content;
		private final boolean skipIndent;

		public Line(int number, String content, boolean skipIndent) {
			this.number = number;
			this.content = content;
			this.skipIndent = skipIndent;
		}

		public int number() {
			return number;
		}

		public String content() {
			return content;
		}

		@Override
		public String toString() {
			return content;
		}

		@Override
		public boolean equals(Object o) {
			return this == o ||
				o instanceof Line && isEquals((Line) o);

		}

		private boolean isEquals(Line o) {
			return skipIndent ? content.trim().equals(o.content.trim()) : content.equals(o.content);
		}

		@Override
		public int hashCode() {
			return content.hashCode();
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			super.clone();
			return new Line(number, content, skipIndent);
		}
	}
}