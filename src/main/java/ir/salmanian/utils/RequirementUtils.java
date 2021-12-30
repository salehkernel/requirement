package ir.salmanian.utils;

import ir.salmanian.models.Requirement;

import java.util.List;

public class RequirementUtils {
    public static int findMaxLevel(List<Requirement> requirements) {
        int maxLevel = 0;
        for (Requirement requirement :
                requirements) {
            if (requirement.getLevel() > maxLevel)
                maxLevel = requirement.getLevel();
        }
        return maxLevel;
    }
}
