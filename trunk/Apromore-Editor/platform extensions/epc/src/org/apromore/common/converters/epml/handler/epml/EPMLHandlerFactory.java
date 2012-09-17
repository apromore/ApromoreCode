/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package org.apromore.common.converters.epml.handler.epml;

import de.epml.*;
import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.apromore.common.converters.epml.handler.epml.impl.*;

public class EPMLHandlerFactory {

    private final EPMLConversionContext context;

    public EPMLHandlerFactory(EPMLConversionContext context) {
        this.context = context;
    }

    public EPMLHandler createNodeConverter(Object obj) {
        if (obj instanceof TypeFunction) {
            return new TypeFunctionHandler(context, (TypeFunction) obj);
        } else if (obj instanceof TypeEvent) {
            return new TypeEventHandler(context, (TypeEvent) obj);
        } else if (obj instanceof TypeAND) {
            return new TypeANDHandler(context, (TypeAND) obj);
        } else if (obj instanceof TypeOR) {
            return new TypeORHandler(context, (TypeOR) obj);
        } else if (obj instanceof TypeXOR) {
            return new TypeXORHandler(context, (TypeXOR) obj);
        } else if (obj instanceof TypeArc) {
            return new TypeArcHandler(context, (TypeArc) obj);
        }
        return null;
    }

    public EPMLHandler createEdgeConverter(Object obj) {
        if (obj instanceof TypeArc) {
            return new TypeArcHandler(context, (TypeArc) obj);
        }
        return null;
    }

}
