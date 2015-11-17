package siani.lasso;

public enum LassoComment {
	XML("<!--", "-->"), JAVA("//", ""), TARA("//", ""), NO_COMMENT("", "");

	private final String begin;
	private final String end;

	LassoComment(String begin, String end) {
		this.begin = begin;
		this.end = end;
	}

	public String begin() {
		return begin;
	}

	public String end() {
		return end;
	}
}
