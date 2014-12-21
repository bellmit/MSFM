package com.cboe.interfaces.application.inprocess;

import java.util.List;

// Base interface for FixCasSession to break vob dependencies between fix and client 
public interface CasSession {
    public Runnable createPendingCancelProcessingTask(List<PendingCancelCacheElement> pendingCancels);
}
