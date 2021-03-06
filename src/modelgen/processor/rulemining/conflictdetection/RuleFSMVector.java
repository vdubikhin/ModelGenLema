package modelgen.processor.rulemining.conflictdetection;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import modelgen.data.complex.Mergeable;
import modelgen.data.pattern.DataPattern;
import modelgen.data.pattern.DataPatterns;
import modelgen.data.pattern.PatternVector;
import modelgen.data.pattern.StateVector;
import modelgen.data.raw.RawDataChunk;
import modelgen.data.state.IState;
import modelgen.data.state.IStateTimeless;
import modelgen.shared.Logger;
import modelgen.shared.Util;

public class RuleFSMVector implements RuleComparable<PatternVector, RuleFSMVector> {
    protected String ERROR_PREFIX = "StatesToPatternConvertert error.";
    protected String DEBUG_PREFIX = "StatesToPatternConverter debug.";
    protected int DEBUG_LEVEL = 1; //TODO: change to property class

    IStateTimeless outputState;
    Map<Integer, DataPattern> outputPatterns;

    Double scaleCoeff;

    public RuleFSMVector(IStateTimeless state, DataPatterns patterns) {
        outputState = state;
        outputPatterns = new HashMap<>();
        scaleCoeff = 1.0;
        for (DataPattern curPattern: patterns) {
            outputPatterns.put(curPattern.getFullRuleVector().getId(), curPattern);
        }
    }

    private void printVectors(Collection<DataPattern> patterns, boolean printRuleVectors) {
        try {
            for (DataPattern curPattern: patterns) {
                PatternVector pattern;
                if (printRuleVectors)
                    pattern = curPattern.getRuleVector();
                else
                    pattern = curPattern.getFullRuleVector();

                Logger.debugPrintln(outputState.getSignalName() + "<" + curPattern.getOutputState().getId()  + ">" +
                        "-(" + curPattern.getOutputState().getTimeStamp().getKey() + ")- "
                        + outputState.convertToString(),
                        DEBUG_LEVEL);
                // Get set of printable names first
                HashSet<String> stateNames = new HashSet<String>();
                for (StateVector stateVector: pattern.values()) {
                    stateNames.addAll(stateVector.keySet());
                }
                
                int stateNum = 0;
                String prefixString = "";
                int leadingSpaceNum = 5;
                for (String stateName: stateNames) {
                    // Reverse order so we print final state last
                    List<Integer> patternVectorKeysReverse = new ArrayList<Integer>(pattern.keySet());
                    Collections.sort(patternVectorKeysReverse);
                    
                    String printLine;
                    int patternVectorId = pattern.getId();

                    prefixString = "";
                    
                    if (stateNum == (int)(stateNames.size()/2)) {
                        prefixString = patternVectorId + ": ";
                        for (int i = prefixString.length(); i < leadingSpaceNum; i++)
                            prefixString = " " + prefixString;
                        
                        printLine = prefixString + stateName + " ";
                    } else {
                        
                        for (int i = 0; i < leadingSpaceNum; i++)
                            prefixString += " ";
                        
                        printLine = prefixString + stateName + " ";
                    }
                    
                    for (Integer key: patternVectorKeysReverse) {
                        StateVector stateVector = pattern.get(key);
                        if (stateVector.containsKey(stateName))
                            printLine += stateVector.get(stateName).getId() + 
                                    stateVector.get(stateName).convertToString() + "-("+ 
                                    stateVector.get(stateName).getTimeStamp().getKey() + "); ";
                    }
                    Logger.debugPrintln(printLine, DEBUG_LEVEL);
                    stateNum++;
                }
                prefixString = "";
                for (int i = prefixString.length(); i < leadingSpaceNum; i++)
                    prefixString = " " + prefixString;
                
                if (stateNames.isEmpty())
                    Logger.debugPrintln(pattern.getId() + " PreSet: " + pattern.getPreSet() + " PostSet: "+
                            pattern.getPostSet(), DEBUG_LEVEL);
                else 
                    Logger.debugPrintln(prefixString + " PreSet: " + pattern.getPreSet() + " PostSet: "+
                            pattern.getPostSet(), DEBUG_LEVEL);

                Logger.debugPrintln("", DEBUG_LEVEL);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }
    }

    public void printFullVectors() {
        printVectors(outputPatterns.values(), false);
    }

    public void printRuleVectors() {
        printVectors(outputPatterns.values(), true);
    }

    @Override
    public void print() {
        Logger.debugPrintln("\nPrinting rule vectors", DEBUG_LEVEL);
        printRuleVectors();
        Logger.debugPrintln("\nPrinting full rule vectors", DEBUG_LEVEL);
        printFullVectors();
    }

    @Override
    public IStateTimeless getOutputState() {
        return outputState;
    }

    @Override
    public List<ConflictComparable<PatternVector, RuleFSMVector>> compareRules(
        RuleFSMVector ruleToCmp, RuleConflictType conflictType) {
        try {
            // List of CSC conflicts
            ArrayList<ConflictComparable<PatternVector, RuleFSMVector>> conflictsList
                    = new ArrayList<ConflictComparable<PatternVector, RuleFSMVector>>();

            Map<Integer, DataPattern> patternsA = this.outputPatterns;
            Map<Integer, DataPattern> patternsB = ruleToCmp.outputPatterns;

            for (DataPattern patternA: patternsA.values()) {
                PatternVector vectorA = patternA.getRuleVector();
                for (DataPattern predicateVectorExtB: patternsB.values()) {
                    PatternVector vectorB = predicateVectorExtB.getRuleVector();

                    if (conflictType == RuleConflictType.RuleVsRulePattern)
                        vectorB = predicateVectorExtB.getRuleVector();

                    if (conflictType == RuleConflictType.RuleVsFullPattern)
                        vectorB = predicateVectorExtB.getFullRuleVector();

                    ConflictComparable<PatternVector, RuleFSMVector> conflict =
                            new ConflictCSC(this, ruleToCmp, vectorA, vectorB, conflictType);

                    //TODO: debug print
//                    System.out.println("Rule " + outputState.getSignalName() + "<" + outputState.getId() + ">: "
//                            + vectorA.getId() + " vs " + 
//                            "Rule " + ruleToCmp.outputState.getSignalName() + "<" + ruleToCmp.outputState.getId() + ">: " +
//                            vectorB.getId() + " - " + vectorA.compareTo(vectorB) + " - " + conflictType);

                    if (conflict.getRuleToFix() != null)
                        conflictsList.add(conflict);
                }
            }

            return conflictsList;
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }

        return null;
    }

    @Override
    public PatternVector getRuleVectorById(Integer id) {
        return outputPatterns.get(id).getRuleVector();
    }

    @Override
    public List<PatternVector> getRulePatterns() {
        return outputPatterns.values().stream()
                .map((s) -> (s.getRuleVector()))
                .collect(Collectors.toList());
    }

    @Override
    public void resetRuleVectorById(Integer id) {
        outputPatterns.get(id).setRuleVector(new PatternVector(id));
    }

    @Override
    public PatternVector getFullRuleVectorById(Integer id) {
        return outputPatterns.get(id).getFullRuleVector();
    }

    @Override
    public void setFullRuleVectorById(Integer id, PatternVector vector) {
        outputPatterns.get(id).setFullRuleVector(vector);
    }

    @Override
    public HashMap<String, RawDataChunk> getAnalogDataById(Integer id) {
        return outputPatterns.get(id).getInputRawData();
    }

    @Override
    public void minimizeRules() {
        try {
            List<DataPattern> rulePatterns = new ArrayList<DataPattern>(outputPatterns.values());
            Mergeable.mergeEntries(rulePatterns);

            outputPatterns = new HashMap<>();
            for (DataPattern curPattern: rulePatterns) {
                //TODO: Corner case - only one pattern present in system so no rule mining is available
                //Construct model from full vector trace
                if (curPattern.getRuleVector().isEmpty())
                    curPattern.setRuleVector(curPattern.getFullRuleVector());

                outputPatterns.put(curPattern.getFullRuleVector().getId(), curPattern);
            }

        } catch (NullPointerException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Null pointer exception.", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }
    }

    @Override
    public void setScaleFactor(Double scale) {
        if (scale != 0) {
            for (DataPattern curPattern: outputPatterns.values()) {
                for (StateVector curVector: curPattern.getRuleVector().values()) {
                    for (IState curState: curVector.values()) {
                        curState.setScale(scale);
                    }
                }
            }
            outputState.setScale(scale);
            scaleCoeff = scale;
        }
    }
    
    @Override
    public Integer getScaleFactor() {
        try {
            Integer scaleFactor = 0;
            for (DataPattern curPattern: outputPatterns.values()) {
                for (StateVector curVector: curPattern.getRuleVector().values()) {
                    for (IState curState: curVector.values()) {
                        scaleFactor = Math.min(curState.getScalePower(), scaleFactor);
                    }
                }

                Double delayLow  = curPattern.getDelayLow();
                Double delayHigh = curPattern.getDelayHigh();

                scaleFactor = Math.min(Util.base10Power(delayLow), scaleFactor);
                scaleFactor = Math.min(Util.base10Power(delayHigh), scaleFactor);
            }

            return scaleFactor;
        } catch (NullPointerException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Null pointer exception.", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }
        return null;
    }

    @Override
    public Entry<Double, Double> getTimeStampById(Integer id) {
        try {
            return outputPatterns.get(id).getOutputState().getTimeStamp();
        } catch (NullPointerException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Null pointer exception.", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }
        return null;
    }

    @Override
    public Map.Entry<Integer, Integer> getDelayById(Integer id) {
        try {
//            Integer delayLow = (int) (outputPatterns.get(id).getDelayLow() * scaleCoeff);
//            Integer delayHigh = (int) (outputPatterns.get(id).getDelayHigh() * scaleCoeff);

            Integer delayLow  = outputPatterns.get(id).getDelayLow().intValue();
            Integer delayHigh = outputPatterns.get(id).getDelayHigh().intValue();

            if (delayLow == null || delayHigh == null)
                return null;

            Map.Entry<Integer, Integer> output = new AbstractMap.SimpleEntry<Integer, Integer>(delayLow, delayHigh);
            return output;
        } catch (NullPointerException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Null pointer exception.", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.errorLoggerTrace(ERROR_PREFIX + " Array out of bounds exception.", e);
        }
        return null;
    }

    @Override
    public String getName() {
        return getOutputState().getSignalName();
    }
}
