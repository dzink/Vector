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

	// PUT operations

	/**
	 * Override to superclass to ensure that only vectors are added to the matrix.
	 */
	put {
		arg n, vector;
		super.put(n, vector.asVector);
		^ this;
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

	// ROW OPERATIONS

	/**
	 * Get the number of rows in the matrix.
	 */
	rowSize {
		this.do {
			arg vector;
			if (vector.notNil) {
				^ vector.size;
			};
		};
		^ 0;
	}

	/**
	 * Return a given row as a vertex.
	 */
	row {
		arg index;
		^ this.collect {
			arg vector;
			vector[index];
		}.asVector();
	}

	/**
	 * Get an array of all rows.
	 */
	rows {
		var rows = Matrix(this.rowSize());
		(this.rowSize).do {
			arg rowIndex;
			rows.add(this.row(rowIndex));
		};
		^ rows;
	}

	/**
	 * Put a row into a given index in the matrix.
	 */
	putRow {
		arg n, vector;
		vector.do {
			arg element, index;
			this[index][n] = element;
		};
	}

	/**
	 * Swap two given rows in place.
	 */
	swapRow {
		arg rowId1, rowId2;
		var row1 = this.row(rowId1);
		var row2 = this.row(rowId2);
		this.putRow(rowId2, row1);
		this.putRow(rowId1, row2);
		^ this;
	}

	/**
	 * Scale a given row in place.
	 */
	scaleRow {
		arg rowId, scalar = 1;
		var vector = this.row(rowId);
		vector = vector * scalar;
		this.putRow(rowId, vector);
		^ this;
	}

	/**
	 * Add a scaled row to another row in place.
	 */
	addRow {
		arg sourceRowId, targetRowId, scalar = 1;
		var source = this.row(sourceRowId);
		var target = this.row(targetRowId);
		source = source * scalar;
		target = target + source;
		this.putRow(targetRowId, target);
		^ this;
	}



	transposition {
		var new = Matrix(this.rowSize());
		this.rowSize.do {
			arg vector, rowIndex;
			new.add(this.row(rowIndex));
		}
		^ new;
	}


	/**
	 * @TODO it would be nice to have a way to "check" a matrix but I'm not sure what that would look like at this time.
	 */
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

	/**
	 * This is how SC will try to multiply arrays, sometimes you want this. Each cell is multiplied by the same cell in other, and a new matrix is returned.
	 */
	hadamard {
		arg other;
		^ super.perform('*', other);
	}

	vectorProduct {
		arg other;
		^ other.collect {
			arg scalar, index;
			(this[index] * scalar);
		}.sum;
	}

	matrixProduct {
		arg other;
		var product = Matrix(this.size, this.vectorSize);
		var result = Matrix.newClear(this.rowSize, other.size);
		this.rowSize.do {
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

	/**
	 * Place a vertex or matrix to the right of this matrix and return a new matrix.
	 */
	augment {
		arg other;
		var m = this.deepCopy();
		other = other.asMatrix.deepCopy();
		if (m.size + other.size > m.maxSize) {
			m.grow(other.size);
		};
		m = m.addAll(*other);
		^ m;
	}

	inverse {
		var m = this.deepCopy();
		m = m.augment(Matrix.identity(m.rowSize));
		m.print;
		^ m;
	}

	/**
	 * Perform a gaussian reduction on a matrix.
	 */
	rowEchelon {
		var result = this.copy;
		(0..(result.rowSize - 2)).do {
			arg i;
			(i..(result.rowSize - 2)).do {
				arg j;
				var rowIndex = j + 1;
				if (result[i][rowIndex] != 0) {
					var diff = result[i][rowIndex] / result[i][i];
					result.addRow(i, rowIndex, diff.neg);
				};
			}
		};
		^ result;
	}

	reducedRowEchelon {
		var result = this.rowEchelon;
		result.rowSize.do {
			arg i;
			var vector = result.row(i);
			result.scaleRow(i, vector[i].reciprocal);
		};
		^ result;
	}

	/**
	 * Solve a set of matrix equations using a gaussian reduction and then backsolving.
	 */
	solve {
		arg solutionVector;
		var solutions = Vector.fill(this.size, 0);
		var reduced = this.augment(solutionVector).reducedRowEchelon();

		// Address the reduced rowSize in reverse order like you learned in Linear algebra.
		((reduced.rowSize - 1)..0).do {
			arg rowIndex;
			var vector = reduced.row(rowIndex);
			var farSide, sum;
			farSide = vector.pop;
			sum = farSide - solutions.dot(vector);
			solutions[rowIndex] = sum;
		};
		^ solutions;
	}

	/**
	 * Return the pivots from a reduced echelon matrix.
	 */
	pivots {
		var pivots = List[];
		this.do {
			arg vector, i;
			if (vector[i] == 1) {
				pivots.add(i);
			};
		}
		^ pivots;
	}

	// NORM OPERATIONS

	/**
	 Frobenius norm.
	 */
	froNorm {
		var f = this.flatten;
		^ f.collect({
			arg n;
			n.squared;
		}).sum.sqrt;
	}

	infNorm {
		var max = 0;
		this.rows().do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}

	l1 {
		var max = 0;
		this.do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}

	print {
		this.rowSize.do {
			arg i;
			var string = "|  ";
			this.row(i).do {
				arg element;
				string = string ++ element.asString() ++ "  ";
			};
			string.postln;
		};
	}

	asMatrix {
		^ this;
	}

	/**
	 * An override to ensure that binary operations return a matrix and not an Array.
	 */
	performBinaryOp {
		arg aSelector, theOperand, adverb;
		var result = super.performBinaryOp(aSelector, theOperand, adverb);
		^ this.class.newFrom(result);
	}

	/**
	 * An override to ensure that binary operations return a matrix and not an Array.
	 */
	performBinaryOpOnSimpleNumber {
		arg aSelector, theOperand, adverb;
		var result = super.performBinaryOpOnSimpleNumber(aSelector, theOperand, adverb);
		^ this.class.newFrom(result);
	}
}
