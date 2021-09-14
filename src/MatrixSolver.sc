/**
 * Algorithms for solving matrices.
 * If you don't want these to write in place, do a deepCopy() of your matrix first.
 */
MatrixSolver {
	*pr_gaussianFactor {
		arg sourceRow, targetRow, columnIndex;
		^ targetRow[columnIndex] / sourceRow[columnIndex]
	}

	/**
	 * Lower-upper decomposition. I would also love to figure out the row swapping matrix.
	 */
	*lu {
		arg m;
		var l = Matrix.identity(m.columnSize);
		var u = m.deepCopy();
		(1..(u.rowSize() - 1)).do {
			arg targetRowId;
			var targetRow = u.row(targetRowId);
			(0..(targetRowId - 1)).do {
				arg sourceRowId;
				var sourceRow = u.row(sourceRowId);
				var factor = this.pr_gaussianFactor(sourceRow, targetRow, sourceRowId);
				l[sourceRowId][targetRowId] = factor;
				targetRow = targetRow - (sourceRow * factor);
			};
			u.putRow(targetRowId, targetRow);
		};
		^ [l, u];
	}

	*upperRowEchelon {
		arg m;
		(0..(m.rowSize - 2)).do {
			arg sourceIndex;
			((sourceIndex + 1)..(m.rowSize - 1)).do {
				arg rowIndex;
				this.pr_calculateRowEchelonRow(m, sourceIndex, rowIndex, sourceIndex);
			};
		};
		^ m;
	}

	*lowerRowEchelon {
		arg m;
		((m.rowSize - 1)..1).do {
			arg sourceIndex;
			((sourceIndex - 1)..0).do {
				arg rowIndex;
				this.pr_calculateRowEchelonRow(m, sourceIndex, rowIndex, sourceIndex);
			}
		};
		^ m;
	}

	*pr_calculateRowEchelonRow {
		arg m, sourceIndex, rowIndex, leftIndex;
		var diff = m[leftIndex][rowIndex] / m[leftIndex][sourceIndex];
		m.addRow(sourceIndex, rowIndex, diff.neg);
	}

	*diagonal {
		arg m;
		m = this.upperRowEchelon(m);
		m = this.lowerRowEchelon(m);
		^ m;
	}

	*inverse {
		arg m;
		m = MatrixReshape.augment(m, Matrix.identity(m.rowSize));
		m = MatrixSolver.diagonal(m).pr_reduceAtDiagonal().chop(m.rowSize, inf);
		^ m;
	}

	*powerIteration {
		arg m;
		var v = Vector.fill(m.rowSize, 1);
		loop {
			var previous = v;
			v = (m * v).normalize;
			if (v == previous) {
				^ v;
			};
		};
	}

	/**
	 * Create a matrix orthonormal to m.
	 */
	*gramSchmidt {
		arg m;
		var previous;
		var maxColumnId = m.columnSize() - 1;
		m[0] = m[0].normalize;
		previous = m[0];
		if (maxColumnId > 0) {
			(1..maxColumnId).do {
				arg i;
				var v = m[i];
				v = v.orthogonalProjectOnto(previous).normalize;
				m[i] = previous = v;
			};
		};
		^ m;
	}

	*qr {
		arg m;
		^ this.gramSchmidt(m);
	}


}
