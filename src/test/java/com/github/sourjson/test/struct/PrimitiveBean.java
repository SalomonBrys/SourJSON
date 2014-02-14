package com.github.sourjson.test.struct;

public class PrimitiveBean {
	public byte		pby;		public Byte			oby;
	public short	psh;		public Short		osh;
	public int		pin;		public Integer		oin;
	public long		plo;		public Long			olo;
	public float	pfl;		public Float		ofl;
	public double	pdo;		public Double		odo;
	public boolean	pbo;		public Boolean		obo;
	public char		pch;		public Character	och;
								public String		str;

	public PrimitiveBean() {}

	public PrimitiveBean(
			byte	pby,		Byte		oby,
			short	psh,		Short		osh,
			int		pin,		Integer		oin,
			long	plo,		Long		olo,
			float	pfl,		Float		ofl,
			double	pdo,		Double		odo,
			boolean	pbo,		Boolean		obo,
			char	pch,		Character	och,
								String		str
			) {
		this.pby = pby;		this.oby = oby;
		this.psh = psh;		this.osh = osh;
		this.pin = pin;		this.oin = oin;
		this.plo = plo;		this.olo = olo;
		this.pfl = pfl;		this.ofl = ofl;
		this.pdo = pdo;		this.odo = odo;
		this.pbo = pbo;		this.obo = obo;
		this.pch = pch;		this.och = och;
							this.str = str;
	}
}
