+ Collection {
	asVector {
		var v = this;
		if (v.isKindOf(Vector).not) {
			v = Vector.newFrom(v);
		}
		^ v;
	}
}

+ Nil {
	asVector {
		^ this;
	}
}

+ Number {
	asVector {
		^ Vector[this];
	}
}
