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

		this.assertEquals(m.rows(), 3, "Matrices accurately count rows.");
		this.assertEquals(m.row(0), Vector[0, 2], "Matrices can get rows.");
	}

	test_vectorProduct {
		var v = Vector[1, 2];
		m = Matrix[[0, 1], [2, 4]];
		this.assertEquals(m.vectorProduct(v), Vector[4, 9], "Vector product is accurately calculated.");
	}

	test_matrixProduct {
		var m2 = Matrix[[1, 2], [3, 1]];
		m = Matrix[[0, 1], [2, 4.1]];
		this.assertEquals(m.matrixProduct(m2), Matrix[[4, 9.2], [2, 7.1]], "Matrix product is accurately calculated.");
	}

	test_hadamard {
		var m2 = Matrix[[1, 2], [3, 1]];
		m = Matrix[[0, 1], [2, 4.1]];
		this.assertEquals(m.hadamard(m2), Matrix[[0, 2], [6, 4.1]], "Hadamard product is accurately calculated.");
	}

	test_solve {
		var v = Vector[-1, 1, 2];
		var solution;
		m = Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2]];
		solution = m.solve(v);

		this.assertEquals(m.augment(v), Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2], [-1, 1, 2]], "Matrix is augmented properly");
		this.assertEquals(m.augment(v).reduce, Matrix[[1, 0, 0], [3, -4, 0], [5, -11, -11.25], [-1, 3, 5.25]], "Matrix is reduced properly.");

		(m.transposition).do {
			arg vector, i;
			this.assertFloatEquals(vector.dot(solution), v[i]);
		};
	}

}
