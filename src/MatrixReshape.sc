MatrixReshape {
	/**
	 * Sort by l2 normalization.
	 */
	*sortColumnsByLength {
		arg m, algo = \l2;
		^ m.sort({
			arg a, b;

			a.perform(algo) < b.perform(algo);
		}).asMatrix();
	}

	*sortRowsByLength {
		arg m, algo = \l2;
		m = this.transpose(m);
		m = this.sortColumnsByLength(m, algo);
		m = this.transpose(m);
		^ m;
	}

	/**
	 * Sort all columns, based on their nth element.
	 */
	*sortColumnsBy {
		arg m, elementId = 0;
		^ m.sort({
			arg a, b;
			a[elementId] < b[elementId];
		}).asMatrix();
	}

	*sortRowsBy {
		arg m, elementId;
		m = this.transpose(m);
		m = this.sortColumnsBy(m, elementId);
		m = this.transpose(m);
		^ m;
	}

 /**
	* Transpose a matrix onto its side and return a new matrix.
	*/
 *transpose {
	 arg m;
	 var new = Matrix(m.rowSize());
	 m.rowSize.do {
		 arg vector, rowIndex;
		 new.add(m.row(rowIndex));
	 }
	 ^ new;
 }

 /**
	* Place a vertex or matrix to the right of this matrix and return a new matrix.
	*/
	*augment {
 		arg m, other;
 		other = other.asMatrix.deepCopy();
 		if (m.columnSize + other.columnSize > m.maxSize) {
 			m.grow(other.columnSize);
 		};
 		m = m.addAll(*other);
 		^ m;
 	}
}
