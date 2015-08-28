package siani.synchronizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block implements Cloneable {
	private List<SynchronizerFile.Line> lines;

	public Block(List<SynchronizerFile.Line> lines) {
		this.lines = Collections.synchronizedList(lines);
	}

	public List<SynchronizerFile.Line> lines() {
		return lines;
	}

	public String getText() {
		return joinBy(SynchronizerFile.NEW_LINE);
	}

	public int size() {
		return lines.size();
	}

	private String joinBy(String link) {
		String text = "";
		for (SynchronizerFile.Line line : lines)
			text += link + line;
		return (text.isEmpty()) ? text : text.substring(link.length());
	}

	public String toString() {
		return getText();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();
		List<SynchronizerFile.Line> clonedLines = new ArrayList<>();
		for (SynchronizerFile.Line line : lines)
			clonedLines.add((SynchronizerFile.Line) line.clone());
		return new Block(clonedLines);
	}
}
