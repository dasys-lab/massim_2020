package massim.scenario.city.data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A product/item in the City scenario.
 */
public class Item {
    private String id;
    private int volume;
    private Map<Item, Integer> requiredItems = new HashMap<>();
    private Set<Tool> toolsNeeded = new HashSet<>();
    private int value;
    private int assembleValue;
    private Map<Item, Integer> requiredBaseItems = new HashMap<>();

    public Item(String id, int volume, int value, Tool... tools){
        this.id = id;
        this.volume = volume;
        Collections.addAll(toolsNeeded, tools);
        this.value = value;
        this.assembleValue = 0;
    }

    /**
     * @return the item's volume
     */
    public int getVolume(){ return volume; }

    /**
     * @return the item's name
     */
    public String getName(){ return id; }

    /**
     * Set the items required to assemble this item.
     * @param requiredItems mapping from items to amounts
     */
    public void setRequiredItems(Map<Item, Integer> requiredItems){
        this.requiredItems = requiredItems;
    }

    public void addRequiredTool(Tool tool){
        toolsNeeded.add(tool);
    }

    /**
     * @return mapping from products to required amounts (original map for now)
     */
    public Map<Item, Integer> getRequiredItems(){
        return requiredItems;
    }

    /**
     * @return a new set containing all tools needed to build this item
     */
    public Set<Tool> getRequiredTools(){
        return new HashSet<>(toolsNeeded);
    }

    /**
     * @return true, if the item needs to be assembled (with other items and/or tools)
     */
    public boolean needsAssembly(){
        return requiredItems.keySet().size() > 0 || toolsNeeded.size() > 0;
    }

    /**
     * @return the item's value
     */
    public int getValue(){ return value; }

    /**
     * @return the item's assembleValue
     */
    public int getAssembleValue(){
        if(assembleValue==0){
            if(needsAssembly()){
                int aValue = 1;
                for(Item item: requiredItems.keySet()){
                    aValue = aValue + requiredItems.get(item) * (item.getAssembleValue());
                }
                this.assembleValue = aValue;
                return aValue;
            }
            return 0;
        }
        return assembleValue;
    }

    /**
     * @return all base items that are needed to build the item and its required items
     */
    public Map<Item, Integer> getRequiredBaseItems(){
        if(requiredBaseItems.isEmpty()){
            if(needsAssembly()){
                // add all base item amounts required for each part
                requiredItems.forEach((reqItem, amount) -> reqItem.getRequiredBaseItems()
                             .forEach((reqBase, baseAmount) -> {
                    int current = requiredBaseItems.getOrDefault(reqBase, 0);
                    requiredBaseItems.put(reqBase, current + amount * baseAmount);
                }));
            }
            else requiredBaseItems.put(this, 1); // no assembly
        }
        return requiredBaseItems;
    }

    @Override
    public String toString(){
        String ret = "Item " + id + ": \tvol("+volume+")\tAV(" + getAssembleValue() + ")";
        if(requiredItems.keySet().size() > 0)
            ret += "\tparts([" + requiredItems.entrySet().stream()
                .map(e -> "(" + e.getValue() + ", " + e.getKey().getName() + ")")
                .collect(Collectors.joining(", ")) + "])";
        if(toolsNeeded.size() > 0)
            ret += "\ttools([" + toolsNeeded.stream().map(Tool::getName).collect(Collectors.joining(", ")) + "])";
        ret += "\treqBaseIt([" + getRequiredBaseItems().entrySet().stream()
                .map(e -> "(" + e.getValue() + ", " + e.getKey().getName() + ")")
                .collect(Collectors.joining(", ")) + "])";

        return ret;
    }
}
