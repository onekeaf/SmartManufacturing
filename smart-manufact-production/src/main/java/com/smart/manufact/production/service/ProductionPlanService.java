package com.smart.manufact.production.service;

import java.util.Map;

public interface ProductionPlanService {
    
    Map<String, Object> createProductionPlan(Map<String, Object> request);
    
    Map<String, Object> getPlanById(String planId);
    
    Map<String, Object> updatePlanStatus(String planId, Integer status);
}
