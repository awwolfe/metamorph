package mchorse.metamorph.capabilities.morphing;

import java.util.List;

import mchorse.metamorph.api.morph.Morph;

/**
 * Morphing interface
 *
 * This interface is responsible for morphing. See {@link Morphing} class for
 * default implementation.
 */
public interface IMorphing
{
    /**
     * Add a morphing 
     */
    public boolean acquireMorph(String name);

    /**
     * Check if this capability has acquired a morph
     */
    public boolean acquiredMorph(String name);

    /**
     * Get all acquired morphings
     */
    public List<String> getAcquiredMorphs();

    /**
     * Set acquired morphings
     */
    public void setAcquiredMorphs(List<String> morphs);

    /**
     * Get current morph 
     */
    public Morph getCurrentMorph();

    /**
     * Get current morph's name 
     */
    public String getCurrentMorphName();

    /**
     * Set morph
     */
    public void setCurrentMorph(String name, boolean creative);

    /**
     * Demorph this capability 
     */
    public void demorph();

    /**
     * Is this capability is morphed at all 
     */
    public boolean isMorphed();
}