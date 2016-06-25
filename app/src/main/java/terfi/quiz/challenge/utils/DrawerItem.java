package terfi.quiz.challenge.utils;

public class DrawerItem {


	
	public static final int DRAWER_ITEM_TAG_LINKED_IN = 26;
	public static final int DRAWER_ITEM_TAG_BLOG = 27;
	public static final int DRAWER_ITEM_TAG_GIT_HUB = 28;
	public static final int DRAWER_ITEM_TAG_INSTAGRAM = 29;
	
	public DrawerItem(int icon, int title) {
		this.icon = icon;
		this.title = title;

	}

	private int icon;
	private int title;
	private int tag;

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
