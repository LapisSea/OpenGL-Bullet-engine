package com.lapissea.opengl.program.rendering.animation.animation;

import com.lapissea.opengl.program.rendering.animation.AnimatedModel;

/**
 * 
 * * Represents an animation that can applied to an {@link AnimatedModel} . It
 * contains the length of the animation in seconds, and a list of
 * {@link KeyFrame}s.
 * 
 * @author Karl
 * 
 *
 */
public class Animation {
	
	private final float length;//in seconds
	private final KeyFrame[] keyFrames;
	
	/**
	 * @param lengthInSeconds
	 *            - the total length of the animation in seconds.
	 * @param frames
	 *            - all the keyframes for the animation, ordered by time of
	 *            appearance in the animation.
	 */
	public Animation(float lengthInSeconds, KeyFrame[] frames) {
		keyFrames = frames;
		length = lengthInSeconds;
	}
	
	/**
	 * @return The length of the animation in seconds.
	 */
	public float getLength() {
		return length;
	}
	
	/**
	 * @return An array of the animation's keyframes. The array is ordered based
	 *         on the order of the keyframes in the animation (first keyframe of
	 *         the animation in array position 0).
	 */
	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}
	
}
