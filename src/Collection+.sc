+ Collection {
	asVector {
		var v = this;
		if (v.isKindOf(Vector).not) {
			v = Vector.newFrom(v);
		}
		^ v;
	}

	asMatrix {
		var m = this;
		if (m.isKindOf(Matrix).not) {
			m = Matrix.newFrom(m);
		};
		^ m;
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
