Vector[float] : FloatArray {

  *unit {
    arg size = 2, x = 0;
    var v = this.new(size);
    v[x] = 1;
    ^ v;
  }

  addSafe {
    arg element;
    if (this.size + 1 > this.maxSize) {
      this.grow(1);
    };
    this.add(element);
    ^ this;
  }

  dropElement {
    arg elementId;
    var v = this.copy;
    v.removeAt(elementId);
    ^ v;
  }

  //
  // clear {
  //   for (0, this.maxSize, {
  //     arg n;
  //     [\clear, n].postln;
  //     this[n] = 0;
  //   })
  //   ^ this;
  // }

  /**
   * For now let's just do the dot.
   */
  inner {
    arg other;
    ^ this.dot(other);
  }

  orthogonalTo {
    arg other;
    ^ this.dot(other) === 0;
  }

  compatibleWith {
    arg other;
    ^ (this.size === other.size);
  }

  notCompatibleWith {
    arg other;
    ^ this.compatibleWith(other).not();
  }

  dot {
    arg other;
    var sum = 0;

    if (this.notCompatibleWith(other)) {
      Exception("%s are not the same size (% and %)".format(this.class, this.size, other.size));
    };

    this.do {
      arg element, i;
      var otherElement = other[i];
      sum = sum + (element * otherElement);
    };
    ^ sum;
  }

  crossable {
    arg other;
    ^ (this.size === 3 and: {other.size === 3});
  }

  cross {
    arg other;
    var v = Vector(3);
    v[0] = (this[1] * other[2]) - (this[2] * other[3]);
    v[1] = (this[2] * other[0]) - (this[0] * other[2]);
    v[2] = (this[0] * other[1]) - (this[1] * other[0]);
    ^ v;
  }

  * {
    arg other;
    if (other.isKindOf(Matrix)) {
      ^ Matrix[this] * other;
    };
    ^ super * other;
  }

  /**
   * If taking the dot of itself, we can calculate a bit faster.
   */
  selfDot {
    var sum = 0;
    this.do {
      arg element;
      sum = sum + element.squared;
    };
    ^ sum;
  }

  /**
   * L1 (Manhattan norm).
   */
  l1 {
    var sum = 0;
    this.do {
      arg element;
      sum = sum + element.abs;
    };
    ^ sum;
  }

  /**
   * L2 (Euclidean norm)
   */
  l2 {
    ^ this.dot(this).sqrt;
  }

  /**
   * Max norm - returns the highest absolute element.
   */
  maxNorm {
    var abs = this.abs;
    ^ abs[abs.maxIndex];
  }

  normalize {
    arg type = \l2;
    var norm = this.perform(type);
    ^ this / norm;
  }

  /**
   * Calculates the cos angle difference between 2 vectors.
   */
  angle {
    arg other;
    var inner = this.inner(other);
    var normProduct = this.l2() * other.l2();
    ^ inner / normProduct;
  }

  homogenous {
    ^ this.copy.addSafe(1);
  }

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

  == {
    arg other, sensitivity = 0.0001;
    if (other.isKindOf(Vector).not) {
      ^ false;
    };
    if (this.size !== other.size) {
      ^ false;
    };
    this.do {
      arg element, row;
      if ((element - other[row]).abs > sensitivity) {
        ^ false;
      };
    };
    ^ true;
  }

  != {
    arg other, sensitivity = 0.0001;
    ^ this.perform('==', other, sensitivity).not();
  }

  asMatrix {
    ^ Matrix[this];
  }

  transpose {
    ^ this.asMatrix.transpose;
  }

  pivotIndex {
    this.do {
      arg element, i;
      if (element != 0) {
        ^ i;
      };
    };
    ^ nil;
  }

  standardDeviation {
    ^ StandardDeviation.fromArray(this);
  }

}
