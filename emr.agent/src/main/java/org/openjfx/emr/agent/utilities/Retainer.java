package org.openjfx.emr.agent.utilities;

public enum Retainer {
	RELIANCE_HMO("RACC-1603-RELIANCE HMO"),
	CLEARLINE_HMO("RACC-2221-CLEARLINE INTERNATIONAL"), 
	SAEED_OIL_AND_GASS("RACC-982-SAEED OIL AND GASS MULTI PURPOSE NIG LTD");
	private String hmo;

	Retainer(String hmo) {
		this.hmo = hmo;
	}
	public String getHMO() {
		return hmo;
	}

}
