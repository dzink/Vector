MatrixSolverTest : MatrixTest {

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
		(m.transpose).do {
			arg vector, i;
			this.assertFloatEquals(vector.dot(solution), v[i]);
		};

		this.assertException({m.solve*()}, Exception, "Throws an error when no solution vector is present.");

		// @TODO weird solutions around zero vector
		// solution = m.solve(Vector[0, 0, 0]).postln;
		// (m.transpose).do {
		// 	arg vector, i;
		// 	this.assertFloatEquals(vector.dot(solution), v[i]);
		// };
	}

	test_inverse {
		m = Matrix[[1, 2, 1],
              [-4, 4, 5],
              [6, 7, 7]];
		this.assertEquals((m.inverse * m).class, Matrix, "Inverse returns a Matrix");
		this.assertEquals((m.inverse * m), Matrix.identity(3), "Inverse creates a proper inverse");
	}

	test_decomposition {
		var l, u;
		m = Matrix[[2, 1, -6], [4, -4, -9], [-4, 3, 5]];
		#l, u = m.lu;

		this.assertEquals(l, Matrix[ Vector[ 1.0, 0.5, -3.0 ], Vector[ 0.0, 1.0, -0.5 ], Vector[ 0.0, 0.0, 1.0 ] ], "L is correctly decomposed.");
		this.assertEquals(u, Matrix[ Vector[ 2.0, 0.0, 0.0 ], Vector[ 4.0, -6.0, 0.0 ], Vector[ -4.0, 5.0, -4.5 ] ], "U is correctly decomposed.");
		this.assertEquals(l * u, m, "LU equals m.");
	}

}
