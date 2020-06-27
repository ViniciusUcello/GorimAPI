package com.gorim.model.forms;

public class Parcela {
	private final int seed;
	private final int seedPrice;
	private final int fertilizer;
	private final int fertilizerPrice;
	private final int pesticideMachine;
	private final int pesticideMachinePrice;
	private final boolean sprayer;
	private final int sprayerPrice;
	
	public Parcela(
			int seed,
			int seedPrice,
			int fertilizer,
			int fertilizerPrice,
			int pesticideMachine,
			int pesticideMachinePrice,
			boolean sprayer,
			int sprayerPrice
	) {
		this.seed = seed;
		this.seedPrice = seedPrice;
		this.fertilizer = fertilizer;
		this.fertilizerPrice = fertilizerPrice;
		this.pesticideMachine = pesticideMachine;
		this.pesticideMachinePrice = pesticideMachinePrice;
		this.sprayer = sprayer;
		this.sprayerPrice = sprayerPrice;
	}

	public int getSeed() {
		return seed;
	}

	public int getSeedPrice() {
		return seedPrice;
	}

	public int getFertilizer() {
		return fertilizer;
	}

	public int getFertilizerPrice() {
		return fertilizerPrice;
	}

	public int getPesticideMachine() {
		return pesticideMachine;
	}

	public int getPesticideMachinePrice() {
		return pesticideMachinePrice;
	}

	public boolean isSprayer() {
		return sprayer;
	}
	
	public int getSprayerPrice() {
		return sprayerPrice;
	}
	
}
