package nodes;

public class Eta extends Node {
    private int index;
    private int environment;
    private Lambda lambda;
	private Id identifier;

    public Eta() {
        super("eta");
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex() {
        return this.index;
    }

    public void setEnvironment(int e) {
        this.environment = e;
    }

    public int getEnvironment() {
        return this.environment;
    }

    public void setLambda(Lambda lambda) {
        this.lambda = lambda;
    }

    public Lambda getLambda() {
        return this.lambda;
    }

	public void setIdentifier(Id id) {
		this.identifier = id;
		
	}

}
