MatrixTest : VectorAbstractTest {
	var m;

	setUp {
		m = Matrix.newClear(4, 4);
	}

	tearDown {
		m.free;
	}

	test_set {
		var v = Vector[3, 4, 5];
		m = Matrix[[1, 2, 3], v, nil];
		this.assertEquals(m.size, 3, "The new matrix is the correct size");
		this.assert(m[0].isKindOf(Vector), "Vectors are added");
		this.assertEquals(m[1].identityHash, v.identityHash, "Actual objects are added when they are vectors");
	}

	test_identity {
		m = Matrix.identity(4);
		this.assertEquals(m, Matrix[[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]], "Identity is set.");
	}

	test_scalar {
		m = Matrix[[1, 2], [3, 4]];
		m = m + 2;
		this.assert(m.isKindOf(Matrix), "m stays a matrix after adding");
		this.assertEquals(m, Matrix[[3, 4], [5, 6]], "Matrix can be added to a scalar");

		m = m * 3;
		this.assertEquals(m, Matrix[[9, 12], [15, 18]], "Matrix can be multiplied by a scalar");
	}

	test_scalarMatrix {
		var m2 = Matrix[[0, 2], [1, 4]];
		m = Matrix[[3, 1], [-3, 2]];
		m = m + m2;
		this.assertEquals(m , Matrix[[3, 3], [-2, 6]], "Matrices are added.");

		m = m * m2;
		this.assertEquals(m , Matrix[[0, 6], [-2, 24]], "Matrices are multiplied by a scalar.");
	}

	test_transposition {
		m = Matrix[[3, 1], [-3, 2]];
		this.assert(m.transposition.isKindOf(Matrix), "Transposition maintains matrix.");
		this.assertEquals(m.transposition(), Matrix[[3, -3], [1, 2]], "Transposition is successful.");

		m = Matrix[[1, 2, 3], [4, 5, 6]];
		this.assertEquals(m.transposition(), Matrix[[1, 4], [2, 5], [3, 6]], "Rectangular matrices are transposed also.");
	}

	test_vectorRow {
		m = Matrix[[0, 1, 4], [2, 4, 5]];

		this.assertEquals(m.rowSize(), 3, "Matrices accurately count rowSize.");
		this.assertEquals(m.row(0), Vector[0, 2], "Matrices can get rows.");
		this.assertEquals(m.row(2), Vector[4, 5], "Matrices can get rows.");
	}

	test_chop {
		m = Matrix[[0, 1, 4], [2, 4, 5], [1, 2, 1], [3, 2, 6], [5, -1, 2]];
		this.assertEquals(m.chop(0, 2), Matrix[[0, 1, 4], [2, 4, 5], [1, 2, 1]], "Chop removes end vectors.");
		this.assertEquals(m.chop(3, inf), Matrix[[3, 2, 6], [5, -1, 2]], "Chop removes start vectors.");
		this.assertEquals(m.chop(1, 3), Matrix[[2, 4, 5], [1, 2, 1], [3, 2, 6]], "Chop removes start and end vectors.");
		this.assertEquals(m.size, 5, "Chop doesn't affect the original matrix.");
	}

	test_rowOperations {
		m = Matrix[[0, 1, 4], [2, 4, 5]];
		m.swapRow(0, 2);
		this.assertEquals(m.row(0), Vector[4, 5], "First row is swapped.");
		this.assertEquals(m.row(2), Vector[0, 2], "Second row is swapped.");
		this.assertEquals(m.row(1), Vector[1, 4], "Middle row is unchanged.");

		m.scaleRow(0, -2);
		this.assertEquals(m.row(0), Vector[-8, -10], "Rows are scaled.");

		m.addRow(0, 1, -0.5);
		this.assertEquals(m.row(1), Vector[5, 9], "Row 1 is scaled and added to row 2.");
		this.assertEquals(m.row(0), Vector[-8, -10], "Row 1 is unchanged.");
	}

	test_reverseRows {
		m = Matrix[[0, 1, 4], [2, 4, 5]];
		m = m.reverseRows();
		this.assertEquals(m.row(0), Vector[4, 5], "Rows are in reverse order.");
		this.assertEquals(m.row(1), Vector[1, 4], "Rows are in reverse order.");
		this.assertEquals(m.row(2), Vector[0, 2], "Rows are in reverse order.");
	}

	test_vectorProduct {
		var v = Vector[1, 2];
		m = Matrix[[0, 1], [2, 4]];
		this.assertEquals(m.vectorProduct(v), Vector[4, 9], "Vector product is accurately calculated.");
	}

	test_matrixProduct {
		var m2 = Matrix[[1, 2], [3, 1]];
		m = Matrix[[0, 1], [2, 4.1]];
		this.assertEquals(m.product(m2), Matrix[[4, 9.2], [2, 7.1]], "Matrix product is accurately calculated.");
		this.assertEquals(m.product(Matrix.identity(m.rowSize)), m, "Identity leaves m intact");
	}

	test_hadamard {
		var m2 = Matrix[[1, 2], [3, 1]];
		m = Matrix[[0, 1], [2, 4.1]];
		this.assertEquals(m.hadamard(m2), Matrix[[0, 2], [6, 4.1]], "Hadamard product is accurately calculated.");
	}

	test_echelon {
		m = Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2]];
		this.assertEquals(m.lowerRowEchelon(), Matrix[Vector[ 4.5, 2.5, 1.0 ], Vector[ 0.0, 5.0, 6.0 ], Vector[ 0.0, 0.0, 2.0 ]], "Lower row echelon format is successful.");
		this.assertEquals(m.upperRowEchelon(), Matrix[Vector[ 1.0, 0.0, 0.0 ], Vector[ 3.0, -4.0, 0.0 ], Vector[ 5.0, -11.0, -11.25 ]], "Upper row echelon format is successful.");
		this.assertEquals(m.diagonal(), Matrix[Vector[ 1.0, 0.0, 0.0 ], Vector[ 0.0, -4.0, 0.0 ], Vector[ 0.0, 0.0, -11.25 ] ], "Diagonal format is successful.");
	}

	test_solve {
		var v = Vector[-1, 1, 2];
		var solution;
		m = Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2]];

		this.assertEquals(m.augment(v), Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2], [-1, 1, 2]], "Matrix is augmented properly");
		this.assertEquals(m.augment(v).rowEchelon, Matrix[[1, 0, 0], [3, -4, 0], [5, -11, -11.25], [-1, 3, 5.25]], "Matrix is converted to row echelon format properly.");
		this.assertEquals(m.augment(v).reducedRowEchelon, Matrix[Vector[1.0, -0.0, -0.0], Vector[3.0, 1.0, -0.0], Vector[5.0, 2.75, 1.0], Vector[-1.0, -0.75, -0.46666666865349]], "Matrix is reduced row echelon format properly.");

		solution = m.solve(v);
		(m.transposition).do {
			arg vector, i;
			this.assertFloatEquals(vector.dot(solution), v[i]);
		};
	}

	test_norm {
		m = Matrix[[1, 2, 3, -1],
			[2, -1, -4, 8],
			[-1, 1, 3, -5],
			[-1, 2, 5, -6],
			[-1, -2, -3, 1]];

		this.assertFloatEquals(m.l1(), 15, "L1 norm is accurately calculated.");
		this.assertFloatEquals((m * 5).l1, 75, "L1 norm is correctly scaled");
		this.assertFloatEquals(m.infNorm(), 21, "InfNorm is accurately calculated.");
		this.assertFloatEquals((m * 5).infNorm, 105, "InfNorm is correctly scaled");
		this.assertFloatEquals(m.froNorm(), 14.730919862656, "FroNorm is accurately calculated.");
		this.assertFloatEquals((m * 5).froNorm, 73.654599313281, "FroNorm is correctly scaled");
	}

	test_rref {
		m = Matrix[[1, 2, 3, -1],
            [2, -1, -4, 8],
            [-1, 1, 3, -5],
            [-1, 2, 5, -6],
            [-1, -2, -3, 1]];
		// m.reducedRowEchelon().print;
		// m.rowEchelon().print;
	}

	test_inverse {
		m = Matrix[[1, 2, 1],
              [-4, 4, 5],
              [6, 7, 7]];
		this.assertEquals(m.inverse.product(m), Matrix.identity(3), "Inverse creates a proper inverse");
	}

	test_mapping {
		var m1 = Matrix[[-1], [0], [1]];
		var m2 = Matrix[[-3, 0, 2]];
		var t = Matrix.scalar(3, -1);
		t.print;
		// m1.product(t).print;
		// t.product(m1 + m2).postln;
		// t.product(m1) + t.product(m2).postln;
	}

}
