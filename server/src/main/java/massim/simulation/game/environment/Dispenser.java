package massim.simulation.game.environment;

import massim.protocol.messages.scenario.data.Thing;

public class Dispenser extends Positionable {

    private String blockType;

    public Dispenser(Position position, String blockType) {
        super(position);
        this.blockType = blockType;
    }

    public String getBlockType() {
        return blockType;
    }

    @Override
    public Thing toPercept() {
        return new Thing(getPosition().x, getPosition().y, Thing.TYPE_DISPENSER, blockType);
    }
}