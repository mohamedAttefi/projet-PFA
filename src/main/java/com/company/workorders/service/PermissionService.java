package com.company.workorders.service;

import com.company.workorders.model.UserRole;
import com.company.workorders.model.Intervention;

/**
 * Service that checks user permissions based on their role.
 */
public class PermissionService {

    /**
     * Check if user can create interventions
     */
    public static boolean canCreateIntervention() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can modify interventions
     */
    public static boolean canModifyIntervention() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can delete interventions
     */
    public static boolean canDeleteIntervention() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can change intervention status
     */
    public static boolean canChangeInterventionStatus() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.TECHNICIAN || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can manage clients
     */
    public static boolean canManageClients() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can manage users (admin only)
     */
    public static boolean canManageUsers() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can view all interventions
     */
    public static boolean canViewAllInterventions() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can view only assigned interventions
     */
    public static boolean canViewOnlyAssignedInterventions() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.TECHNICIAN;
    }

    /**
     * Check if user can add comments
     */
    public static boolean canAddComments() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.TECHNICIAN || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can view statistics
     */
    public static boolean canViewBasicStatistics() {
        return true; // All roles can view basic dashboard
    }

    /**
     * Check if user can view advanced statistics
     */
    public static boolean canViewAdvancedStatistics() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can assign technicians
     */
    public static boolean canAssignTechnicians() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
    }

    /**
     * Check if user can reassign interventions
     */
    public static boolean canReassignInterventions() {
        UserRole role = getCurrentUserRole();
        return role == UserRole.ADMINISTRATOR;
    }

    /**
     * Get the current user's role
     */
    private static UserRole getCurrentUserRole() {
        return SessionContext.getCurrentUser() != null 
            ? SessionContext.getCurrentUser().getRole() 
            : UserRole.RECEPTIONIST; // Default to RECEPTIONIST
    }

    /**
     * Get the current user's ID
     */
    public static long getCurrentUserId() {
        return SessionContext.getCurrentUser() != null 
            ? SessionContext.getCurrentUser().getId() 
            : -1;
    }

    /**
     * Check if user can see this intervention
     * Technician can only see assigned interventions
     */
    public static boolean canViewIntervention(Intervention intervention) {
        UserRole role = getCurrentUserRole();
        long currentUserId = getCurrentUserId();
        
        if (role == UserRole.ADMINISTRATOR || role == UserRole.RECEPTIONIST) {
            return true; // Can see all
        }
        
        if (role == UserRole.TECHNICIAN) {
            // Can only see if assigned to them
            return intervention.getAssignedTo() == currentUserId;
        }
        
        return false;
    }

    /**
     * Check if user can accept/start an intervention
     */
    public static boolean canAcceptIntervention(Intervention intervention) {
        UserRole role = getCurrentUserRole();
        long currentUserId = getCurrentUserId();
        
        if (role == UserRole.TECHNICIAN) {
            return intervention.getAssignedTo() == currentUserId;
        }
        
        return role == UserRole.ADMINISTRATOR;
    }
}
