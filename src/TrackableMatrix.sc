/**
 * As this matrix is operated on, it will maintain its inverse.
 */
TrackableMatrix : Matrix {
	var swaps;
	var identity;

	*newFrom {
		arg source;
		var m = super.newFrom(source);
		^ m.init()
	}

	init {
		swaps = this.rightIdentity();
		identity = this.rightIdentity();
		^ this;
	}

	// @TODO
}
