package ch.hsr.appquest.coincollector;

import java.util.List;

public class CoinRegion {

    private Identifier identifier;
    private List<Coin> coinList;

    CoinRegion(Identifier identifier, List<Coin> coinList) {
        this.identifier = identifier;
        this.coinList = coinList;
    }

    public Identifier getIdentifier() { return identifier; }

    public List<Coin> getCoinList() { return coinList; }

    public enum Identifier {
        lakeside,
        island,
        cafeteria,
        bicyclestand,
        researchbuilding
    }

    public String getImageName() {
        switch (identifier) {
            case lakeside: return "Seeufer";
            case island: return "Insel";
            case cafeteria: return "Mensa";
            case bicyclestand: return "Veloständer";
            case researchbuilding: return "Forschungsgebäude";
            default: return "";
        }
    }

    public String getRegionName() {
       return getImageName();
    }

}
