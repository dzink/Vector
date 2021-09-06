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

	*columns {
		arg ... columns;
		var m = this.new();
		columns.do {
			arg column;
			m.grow(1);
			m.add(column);
		};
		^ m;
	}

	*rows {
		arg ... rows;
		var m = this.newClear(rows[0].size, rows.size);
		rows.do {
			arg row, i;
			m.putRow(i, row);
		};
		^ m;
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

	// COLUMN OPERATIONS

	chop {
		arg first = 0, last = inf;
		if (last == inf) {
			last = this.columnSize;
		};
		^ this[first..last];
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

	dimensions {
		^ this.rowSize();
	}

	columnSize {
		^ this.size;
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

	reverseRows {
		var m = this.deepCopy();
		var rowSize = m.rowSize();
		rowSize.do {
			arg i;
			m.putRow(i, this.row(rowSize - 1 - i));
		};
		^ m;
	}

	/**
	 * Sorting.
	 */

	 /**
	  * Sort all columns, based on their nth element.
		*/
	 sortColumnsBy {
		 arg elementId = 0;
		 ^ this.sort({
			 arg a, b;
			 a[elementId] < b[elementId];
		 }).asMatrix();
	 }

	 /**
	  * Sort all rows, based on their nth element.
		*/
	 sortRowsBy {
		 arg elementId;
		 var rows = this.rows.sort({
			 arg a, b;
			 a[elementId] < b[elementId];
		 });
		 ^ rows.asMatrix.transpose;
	 }

	/**
	 * Transpose a matrix onto its side and return a new matrix.
	 */
	transpose {
		var new = Matrix(this.rowSize());
		this.rowSize.do {
			arg vector, rowIndex;
			new.add(this.row(rowIndex));
		}
		^ new;
	}

	/**
	 * Place a vertex or matrix to the right of this matrix and return a new matrix.
	 */
	augment {
		arg other;
		var m = this.deepCopy();
		^ m.pr_augment(other);
	}

	pr_augment {
		arg other;
		other = other.asMatrix.deepCopy();
		if (this.columnSize + other.columnSize > this.maxSize) {
			this.grow(other.columnSize);
		};
		^ this.addAll(*other);
	}

	/**
	 * Convenience methods to create the right kinds of identities based on the current matrix.
	 */
	identity {
		arg scalar = 1;
		^ this.rightIdentity(scalar);
	}

	leftIdentity {
		arg scalar = 1;
		^ Matrix.scalar(this.rowSize, scalar);
	}

	rightIdentity {
		arg scalar = 1;
		^ Matrix.identity(this.columnSize, scalar);
	}

	rotation {
		arg ... radians;
		// @TODO
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
		^ (this.asArray * other.asArray).asMatrix;
	}

	vectorProduct {
		arg other;

		if (this.notCompatibleWith(other)) {
			Exception("% * % product requires equal column (%) and vector (%) sizes.".format(this.class, other.class, this.columnSize, other.size)).throw();
		};

		^ other.collect {
			arg scalar, index;
			(this[index] * scalar);
		}.sum;
	}

	compatibleWith {
		arg other;
		if (other.isKindOf(Matrix)) {
			^ (this.columnSize() === other.rowSize());
		};
		if (other.isKindOf(Vector)) {
			^ (this.columnSize() === other.size());
		};
		^ true;
	}

	notCompatibleWith {
		arg other;
		^ this.compatibleWith(other).not();
	}

	matrixProduct {
		arg other;
		var result;

		if (this.notCompatibleWith(other)) {
			Exception("% * % product requires equal column (%) and row (%) sizes.".format(this.class, other.class, this.columnSize.cs, other.rowSize.cs)).throw();
		};

		^ other.collect {
			arg otherVector;
			this.vectorProduct(otherVector);
		}.asMatrix();
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

	inverse {
		var m = this.deepCopy();
		m = m.augment(Matrix.identity(m.rowSize));
		m = m.pr_diagonal().pr_reduceAtDiagonal().chop(m.rowSize, inf);
		^ m;
	}

	pr_gaussianFactor {
		arg sourceRow, targetRow, columnIndex;
		^ targetRow[columnIndex] / sourceRow[columnIndex]
	}

	lu {
		var l = Matrix.identity(this.columnSize);
		var u = this.deepCopy();
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

	pivot {
		// @TODO
		// var pivots = Matrix.identity(this.columnSize);
		// var m = this.deepCopy();
		// m.rowSize.do {
		// 	arg i;
		// 	var sourceRow =
		// };
	}

	pr_checkPivot {
		arg row, rowIndex;
		return (row[rowIndex] != 0);
	}

	/**
	 * Perform a gaussian reduction on a matrix.
	 */
	upperRowEchelon {
		var result = this.deepCopy();
		^ result.pr_upperRowEchelon();
	}

	pr_upperRowEchelon {
		(0..(this.rowSize - 2)).do {
			arg sourceIndex;
			((sourceIndex + 1)..(this.rowSize - 1)).do {
				arg rowIndex;
				this.pr_calculateRowEchelonRow(sourceIndex, rowIndex, sourceIndex);
			}
		};
		^ this;
	}

	rowEchelon {
		^ this.upperRowEchelon();
	}

	lowerRowEchelon {
		var result = this.deepCopy();
		^ result.pr_lowerRowEchelon();
	}

	pr_lowerRowEchelon {
		((this.rowSize - 1)..1).do {
			arg sourceIndex;
			((sourceIndex - 1)..0).do {
				arg rowIndex;
				this.pr_calculateRowEchelonRow(sourceIndex, rowIndex, sourceIndex);
			}
		};
		^ this;
	}

	diagonal {
		var m = this.deepCopy();
		^ m.pr_diagonal();
	}

	pr_diagonal {
		^ this.pr_upperRowEchelon().pr_lowerRowEchelon();
	}

	pr_calculateRowEchelonRow {
		arg sourceIndex, rowIndex, leftIndex;
		var diff = this[leftIndex][rowIndex] / this[leftIndex][sourceIndex];
		this.addRow(sourceIndex, rowIndex, diff.neg);
	}

	/**
	 * Reduce each row at diagonal.
	 */
	reduceAtDiagonal {
		var m = this.deepCopy();
		^ m.pr_reduceAtDiagonal();
	}

	pr_reduceAtDiagonal {
		this.rows.do {
			arg vector, i;
			this.scaleRow(i, vector[i].reciprocal);
		};
		^ this;
	}

	reducedRowEchelon {
		var m = this.deepCopy();
		^ m.rowEchelon().pr_reduceAtDiagonal()
	}

	/**
	 * Solve a set of matrix equations using a gaussian reduction and then backsolving.
	 * Another option is m.diagonal.reduceAtDiagonal.last - this takes 50% longer, @TODO look into relative accuracy
	 */
	solve {
		arg solutionVector;
		var reduced;
		if (solutionVector.isNil) {
			Exception("% requires a vector to solve for.".format(this.class)).throw();
		};
		reduced = this.augment(solutionVector).reducedRowEchelon();

		// @TODO check for viable solutions
		^ reduced.pr_backsolve();
	}

	pr_backsolve{
		var solutions = Vector.fill(this.columnSize - 1, 0);

		// Address the reduced rowSize in reverse order like you learned in Linear algebra.
		((this.rowSize - 1)..0).do {
			arg rowIndex;
			var vector = this.row(rowIndex);
			var farSide, sum;
			farSide = vector.pop;
			sum = farSide - solutions.dot(vector);
			solutions[rowIndex] = sum;
		};
		^ solutions;
	}

	minors {

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

	determinant {
		if (this.size == 2) {
			^ (this[0][0] * this[1][1]) - (this[0][1] * this[1][0]);
		} {
			var total = 0;
			var evenOdd = 1;
			this.do {
				arg vector, i;
				var subDeterminant = this.pr_dropColumnAndRow(i, 0).determinant;
				subDeterminant = subDeterminant * vector[0] * evenOdd;
				total = total + subDeterminant;
				evenOdd = evenOdd * -1;
			};
			^ total;
		};

	}

	pr_dropColumnAndRow {
		arg columnId, rowId;
		var m = Matrix.new();
		if (columnId > 0) {
			m = m ++ this[0..(columnId - 1)];
		};
		if (columnId < (this.size - 1)) {
			m = m ++ this[(columnId + 1)..(this.size - 1)];
		};
		m = m.deepCopy();
		^ m.collect {
			arg vector;
			vector.dropElement(rowId);
		}.asMatrix;
	}

	regression {
		// @TODO
		// arg dependentElementId = 0, independentElementId = 1;
		// var variables = Vector[dependentElementId] ++ independentElementId.asVector;
		// var rows = this.rows.at(variables);
		// var independentRows = rows[1..(rows.size - 1)];
		// var dependentSum = rows[0].sum;
		// var independentSums = independentRows.collect({
		// 	arg row;
		// 	row.sum;
		// }).postln;
		//
		// ^ rows;
		// // variables.postln;
	}

	isEigenvalue {
		arg n;
		var lambdaI = Matrix.scalar(this.columnSize, n);
		^ (this - lambdaI).determinant == 0;
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

	asArray {
		var a = Array(this.size);
		this.do {
			arg vector;
			a.add(vector.asArray);
		};
		^ a;
	}

	/**
	 * An override to ensure that binary operations return a matrix and not an Array.
	 * Make sure to use dot products on multiplication of vectors and matrices.
	 */
	performBinaryOp {
		arg aSelector, theOperand, adverb;
		var result;
		if (aSelector === '*') {
			if (theOperand.isKindOf(Vector)) {
				^ this.vectorProduct(theOperand);
			};
			if (theOperand.isKindOf(Matrix)) {
				^ this.matrixProduct(theOperand);
			};
		};
		result = super.performBinaryOp(aSelector, theOperand, adverb);
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
