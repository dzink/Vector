MatrixRowOperations {
	*swap {
		arg m, rowId1, rowId2;
		var row1 = m.row(rowId1);
		var row2 = m.row(rowId2);
		m.putRow(rowId2, row1);
		m.putRow(rowId1, row2);
		^ m;
	}

	*scale {
		arg m, rowId, scalar = 1;
		var vector = m.row(rowId);
		vector = vector * scalar;
		m.putRow(rowId, vector);
		^ m;
	}

	*add {
		arg m, sourceRowId, targetRowId, scalar = 1;
		var source = m.row(sourceRowId);
		var target = m.row(targetRowId);
		source = source * scalar;
		target = target + source;
		m.putRow(targetRowId, target);
		^ m;
	}

	*reverseOrder {
		arg m;
		var maxIndex = m.rowSize - 1;
		(m.rowSize / 2).floor.do {
			arg rowId;
			this.swap(m, rowId, maxIndex - rowId);
		};
		^ m;
	}

	*augment {
		arg m, other;
		other = other.asMatrix.deepCopy();
		if (m.columnSize + other.columnSize > m.maxSize) {
			m.grow(other.columnSize);
		};
		m = m.addAll(*other);
		^ m;
	}

	findPivot {
		arg m, rowId, minimum = 0;
		
	}

	*reduceRowAtPivot {
		arg m, row;
	}
}
