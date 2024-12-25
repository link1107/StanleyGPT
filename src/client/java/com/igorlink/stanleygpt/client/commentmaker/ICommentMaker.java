package com.igorlink.stanleygpt.client.commentmaker;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for comment makers.
 */
public interface ICommentMaker {

    /**
     * Makes a new comment.
     *
     * @param eventPriority    the priority of the event
     * @param eventDescription the description of the event
     * @param takeScreenshot   whether to take a screenshot
     */
    void makeNewComment(int eventPriority, @Nullable String eventDescription, boolean takeScreenshot);

}
