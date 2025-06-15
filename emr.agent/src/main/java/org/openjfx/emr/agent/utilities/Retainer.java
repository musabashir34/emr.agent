package org.openjfx.emr.agent.utilities;

public enum Retainer {
	RELIANCE_HMO("RELIANCE HMO"),
	CLEARLINE_HMO("CLEARLINE INTERNATIONAL"), 
	SAEED_OIL_AND_GASS("SAEED OIL AND GAS MULTIPURPOSE NIG LTD");
	private String hmo;

	Retainer(String hmo) {
		this.hmo = hmo;
	}
	public String getHMO() {
		return hmo;
	}

}
