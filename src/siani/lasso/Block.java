package siani.lasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Block implements Cloneable {
	private List<LassoFile.Line> lines;

	public Block(List<LassoFile.Line> lines) {
		this.lines = Collections.synchronizedList(lines);
	}

	public List<LassoFile.Line> lines() {
		return lines;
	}

	public String getText() {
		return joinBy(LassoFile.NEW_LINE);
	}

	public int size() {
		return lines.size();
	}

	private String joinBy(String link) {
		String text = "";
		for (LassoFile.Line line : lines)
			text += link + line;
		return (text.isEmpty()) ? text : text.substring(link.length());
	}

	public String toString() {
		return size() + ": " + getText();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();
		List<LassoFile.Line> clonedLines = new ArrayList<>();
		for (LassoFile.Line line : lines)
			clonedLines.add((LassoFile.Line) line.clone());
		return new Block(clonedLines);
	}
}
