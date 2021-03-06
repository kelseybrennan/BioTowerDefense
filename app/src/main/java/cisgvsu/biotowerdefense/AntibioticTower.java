package cisgvsu.biotowerdefense;

import android.content.res.Resources;
import android.util.Log;
import android.util.Range;

/**
 * This class models an antibiotic "tower" that shoots
 * a dosage of a certain type of antibiotic at the different
 * target.
 *
 * Created by Ella on 8/31/2017.
 */

public class AntibioticTower {
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int fifthWidth = screenWidth / 5;

    /** Type of antibiotic that this tower shoots. */
    private AntibioticType type;

    /** Power of each antibiotic "dosage." */
    private int power;

    /** Cost to buy this type of tower. */
    private int cost;

    /** Location of tower in game, 0 - 4 to begin with */
    private int location;
    private int minRange;
    private int maxRange;

    /** Whether or not the tower is shooting at target (thread control). */
    private boolean shooting;
    private boolean addPill;

    /**
     * Create a default penicillin tower at the first location.
     */
    public AntibioticTower() {
        this.type = AntibioticType.penicillin;
        this.power = AntibioticType.getPower(type);
        this.cost = AntibioticType.getCost(type);
        this.location = 0;
        this.maxRange = screenWidth;
        this.minRange = screenWidth - fifthWidth;
    }

    /**
     * Create a new tower of the specified type and at the given location.
     * @param type Type of antibiotic for this tower to shoot.
     * @param location Location of tower in game.
     */
    public AntibioticTower(AntibioticType type, int location) {
        this.type = type;
        this.power = AntibioticType.getPower(type);
        this.cost = AntibioticType.getCost(type);
        this.location = location;

        //set the reach of the tower
        switch (location) {
            case 0:
                this.maxRange = screenWidth;
                this.minRange = screenWidth - fifthWidth;
                Log.d("*************", "Tower 0, max = " + maxRange + " min = " + minRange);
                break;
            case 1:
                this.maxRange = screenWidth - fifthWidth;
                this.minRange = screenWidth - 2*fifthWidth;
                Log.d("*************", "Tower 1, max = " + maxRange + " min = " + minRange);
                break;
            case 2:
                this.maxRange = screenWidth - 2*fifthWidth;
                this.minRange = screenWidth - 3*fifthWidth;
                Log.d("*************", "Tower 2, max = " + maxRange + " min = " + minRange);
                break;
            case 3:
                this.maxRange = screenWidth - 3*fifthWidth;
                this.minRange = screenWidth - 4*fifthWidth;
                Log.d("*************", "Tower 3, max = " + maxRange + " min = " + minRange);
                break;
            case 4:
                this.maxRange = screenWidth - 4*fifthWidth;
                this.minRange = 0;
                Log.d("*************", "Tower 4, max = " + maxRange + " min = " + minRange);
                break;

        }
    }

    public int getLocation() {
        return this.location;
    }

    public void setLocation(int l) {
        this.location = l;
    }

    /**
     * Get the type of the tower.
     * @return Antibiotic that this tower shoots.
     */
    public AntibioticType getType() {
        return type;
    }

    /**
     * Set the type of the tower.
     * @param type The new antibiotic that this tower shoots.
     */
    public void setType(AntibioticType type) {
        this.type = type;
    }

    /**
     * Get the tower's power.
     * @return Power of one antibiotic "dosage".
     */
    public int getPower() {
        return power;
    }

    /**
     * Set the power for the tower.
     * @param power New power of one antibiotic "dosage".
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Get the cost of the tower.
     * @return Cost of the tower.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Set the cost of the tower.
     * @param cost New cost of the tower.
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Get whether or not the target is in range of the tower
     * @param x the x-coordinate of the target
     * @return True if it is in range, false otherwise
     */
    public boolean inRange(int x) {
        if (x <= this.maxRange && x >= this.minRange) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the max range for this tower.
     * @return int representing max X coordinate
     */
    public int getMaxRange() {
        return this.maxRange;
    }

    /**
     * Get the min range for this tower.
     * @return int representing min X coordinate
     */
    public int getMinRange() {
        return this.minRange;
    }

    /**
     * Get whether or not this tower is shooting.
     * @return True if shooting, false otherwise.
     */
    public boolean getShooting() {
        return this.shooting;
    }

    /**
     * Set whether or not this tower is shooting,
     * @param newVal New value for whether or not this tower is shooting.
     */
    public void setShooting(boolean newVal) {
        this.shooting = newVal;
    }

    /**
     * Get if a new pill can be added for this tower
     * @return True if a new pill can be added, false otherwise
     */
    public boolean addPill() {
        return addPill;
    }

    /**
     * Set whether or not a new pill can be added
     * @param addPill if a new pill can be added
     */
    public void setAddPill(boolean addPill) {
        this.addPill = addPill;
    }
}