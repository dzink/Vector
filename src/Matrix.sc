Matrix[slot] : Array {
	var <> vectorSize;

	*new {
		arg size, vectorSize;
		var m = super.new(size);
		m.vectorSize = vectorSize;
		^ m;
	}

	*newClear {
		arg size, vectorSize;
		var m = this.new(size);
		size.do {
			m.add(Vector.newClear(vectorSize));
		};
		^ m;
	}

	*scalar {
		arg size, scalar = 1;
		var m = this.newClear(size, size);
		size.do {
			arg i;
			m[i][i] = scalar;
		};
		^ m;
	}

	*identity {
		arg size;
		^ this.scalar(size, 1);
	}

	rows {
		this.do {
			arg vector;
			if (vector.notNil) {
				^ vector.size;
			};
		};
		^ 0;
	}

	row {
		arg index;
		^ this.collect {
			arg vector;
			vector[index];
		}.asVector();
	}

	transposition {
		var new = Matrix(this.rows());
		this.rows.do {
			arg vector, rowIndex;
			new.add(this.row(rowIndex));
		}
		^ new;
	}

	put {
		arg n, vector;
		super.put(n, vector.asVector);
		^ this;
	}

	putRow {
		arg n, vector;
		vector.do {
			arg element, index;
			this[index][n] = element;
		};
	}

	insert {
		arg vector;
		super.insert(vector.asVector);
		^ this;
	}

	add {
		arg vector;
		super.add(vector.asVector);
		^ this;
	}

	checkVectors {
		var size = nil;
		this.do {
			arg vector;
			if (vector.notNil) {
				if (vector.isKindOf(Vector).not) {
					// @TODO throw error here.
				};
			};
			if (vector.size != size) {
				if (size.notNil) {
					// @TODO throw error here.
				};
				size = vector.size;
			};
		};
		^ this;
	}

	// dot {
	// 	arg other;
	// }
	//
	// rowVector {
	// 	arg n;
	// 	^ this.collect {
	// 		arg column;
	// 		column[n];
	// 	};
	// }
	//
	// inv {
	//
	// }

	performBinaryOp {
    arg aSelector, theOperand, adverb;
    var result = super.performBinaryOp(aSelector, theOperand, adverb);
    ^ this.class.newFrom(result);
  }

	performBinaryOpOnSimpleNumber {
    arg aSelector, theOperand, adverb;
    var result = super.performBinaryOpOnSimpleNumber(aSelector, theOperand, adverb);
    ^ this.class.newFrom(result);
  }

	// == {
	// 	arg other, sensitivity = 0.0001;
	// 	if (other.size != this.size) {
	// 		^ false;
	// 	};
	// 	this.do {
	// 		arg vector, column;
	// 		var otherVector = other[column];
	// 		[\test, vector, otherVector].postln;
	// 		if (vector.notNil) {
	// 			if (vector != otherVector) {
	// 				^ false;
	// 			};
	// 		};
	// 	};
	// 	^ true;
	// }
	hadamard {
		arg other;
		^ super.perform('*', other);
	}

	vectorProduct {
		arg other;
		// var vector = Vector.newClear(other.size);
		^ other.collect {
			arg scalar, index;
			(this[index] * scalar);
		}.sum;
		// ^ vector;
	}

	matrixProduct {
		arg other;
		var product = Matrix(this.size, this.vectorSize);
		var result = Matrix.newClear(this.rows, other.size);
		this.rows.do {
			arg thisIndex;
			var rowVector = this.row(thisIndex);
			other.do {
				arg otherVector, otherIndex;
				result[otherIndex][thisIndex] = rowVector.dot(otherVector);
			};
		};
		^ result;
	}

	product {
		arg other;
		if (other.isKindOf(Vector)) {
			^ this.vectorProduct(other);
		};
		if (other.isKindOf(Matrix)) {
			^ this.matrixProduct(other);
		};
		^ super.perform('*', other);
	}

	augment {
		arg vector;
		var m = this.deepCopy();
		if (m.size + 1 > m.maxSize) {
			m.grow(1);
		};
		m.add(vector.copy);
		^ m;
	}

	/**
	 * Perform a gaussian reduction on a matrix.
	 */
	reduce {
		var result = this.copy;
		(0..(result.rows - 2)).do {
			arg i;
			(i..(result.rows - 2)).do {
				arg j;
				var rowIndex = j + 1;
				if (result[i][rowIndex] != 0) {
					var diff = result[i][rowIndex] / result[i][i];
					var newRow = result.row(rowIndex) - (result.row(i) * diff);
					result.putRow(rowIndex, newRow);
				};
			}
		};
		^ result;
	}

	/**
	 * Solve a set of matrix equations using a gaussian reduction and then backsolving.
	 */
	solve {
		arg solutionVector;
		var solutions = Vector.fill(this.size, 0);
		var reduced = this.augment(solutionVector).reduce();

		// Address the reduced rows in reverse order like you learned in Linear algebra.
		((reduced.rows - 1)..0).do {
			arg rowIndex;
			var vector = reduced.row(rowIndex);
			var farSide, sum, scalar;
			farSide = vector.pop;
			sum = farSide - solutions.dot(vector);
			scalar = vector[rowIndex];
			solutions[rowIndex] = sum / scalar;
		};
		^ solutions;
	}

	print {
		this.rows.do {
			arg i;
			var string = "|  ";
			this.row(i).do {
				arg element;
				string = string ++ element.asString() ++ "  ";
			};
			string.postln;
		};
	}
}
