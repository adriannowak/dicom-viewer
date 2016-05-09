/*******************************************************************************
 * Copyright (c) 2010 Nicolas Roduit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 ******************************************************************************/
package utils;

import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.registry.RenderableRegistryMode;
import javax.media.jai.registry.RenderedRegistryMode;

public class RectifySignedShortDataDescriptor extends OperationDescriptorImpl implements RenderedImageFactory {

	private static final long serialVersionUID = 1L;

	/**
     * The resource strings that provide the general documentation and specify the parameter list for this operation.
     */
    private static final String[][] resources = { { "GlobalName", "RectifySignedShortData" },  

        { "LocalName", "RectifySignedShortData" },  

        { "Vendor", "" },  

        { "Description", 
            "Operation to read correctly signed images (9-15 bits) (Workaround for imageio codecs issue" }, 

        { "DocURL", "" },  

        { "Version", "1.0" },  

        { "arg0Desc", "Number of signifative bits" } };  

    private static final Class<?>[] paramClasses = { int[].class };

    /** The parameter name list for this operation. */
    private static final String[] paramNames = { "bits" }; 

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = { new int[] { 0 } };

    private static final String[] supportedModes = { "rendered", "renderable" };  

    /** Constructor. */
    public RectifySignedShortDataDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    /**
     * Validates the input source and parameter.
     *
     * <p>
     * In addition to the standard checks performed by the superclass method, this method checks that the source image
     * has an integral data type and that "shift" has length at least 1.
     */
    @Override
    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message) {
        if (!super.validateArguments(modeName, args, message)) {
            return false;
        }

        if (!modeName.equalsIgnoreCase("rendered")) { 
            return true;
        }

        RenderedImage src = args.getRenderedSource(0);

        int dtype = src.getSampleModel().getDataType();

        if (dtype != DataBuffer.TYPE_SHORT) {
            return false;
        }

        int length = ((int[]) args.getObjectParameter(0)).length;

        if (length < 1) {
            return false;
        }

        return true;
    }

    @Override
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = null;
        if(renderHints != null){
        	layout = ImageLayout.class.cast(renderHints.get(JAI.KEY_IMAGE_LAYOUT));
        }

        return new RectifySignedShortDataOpImage(args.getRenderedSource(0), renderHints, layout,
            (int[]) args.getObjectParameter(0));
    }

    public static RenderedOp create(RenderedImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("RectifySignedShortData", RenderedRegistryMode.MODE_NAME); 

        pb.setSource("source0", source0); 

        pb.setParameter("bits", constants); 

        return JAI.create("RectifySignedShortData", pb, hints); 
    }

    public static RenderableOp createRenderable(RenderableImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("RectifySignedShortData", RenderableRegistryMode.MODE_NAME); 

        pb.setSource("source0", source0); 

        pb.setParameter("bits", constants); 

        return JAI.createRenderable("RectifySignedShortData", pb, hints); 
    }
}
