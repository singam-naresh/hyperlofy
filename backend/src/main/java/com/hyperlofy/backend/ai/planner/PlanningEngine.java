package com.hyperlofy.backend.ai.planner;

public interface PlanningEngine {
    PlanningResponse orchestrate(PlanningRequest request);
}
