package EarthSim;

public enum GridSpacingIncrement {
	_1(1), _2(2), _3(3), _4(4), _5(5), _6(6), _9(9), _10(10), _12(12), _15(15), _18(
			18), _20(20), _30(30), _36(36), _45(45), _60(60), _90(90), _180(180);

	private int numVal;

	GridSpacingIncrement(int numVal) {
		this.numVal = numVal;
	}

	public int getNumVal() {
		return numVal;
	}

	public static GridSpacingIncrement getEnumVal(int numVal) {
		if(numVal < 1){
			return GridSpacingIncrement._1;
		}
		if(numVal > 180){
			return GridSpacingIncrement._180;
		}
		
		for (int i = 0; i < GridSpacingIncrement.values().length; i++) {
			GridSpacingIncrement item = GridSpacingIncrement.values()[i];
			GridSpacingIncrement nextItem;
			if (item == GridSpacingIncrement._180) {
				nextItem = GridSpacingIncrement._180;
			} else {
				nextItem = GridSpacingIncrement.values()[i + 1];
			}

			if (numVal == item.getNumVal()) {
				return item;
			} else {
				if (numVal > item.getNumVal() && numVal < nextItem.getNumVal()) {
					// Fallback to lower value
					return item;
				}
			}

		}
		return GridSpacingIncrement._1;
	}

	@Override
	public String toString() {
		String strValue = Integer.toString(this.getNumVal()) + " Degree";
		if (this.getNumVal() > 1) {
			strValue = strValue + "s";
		}
		return strValue;
	}
}
