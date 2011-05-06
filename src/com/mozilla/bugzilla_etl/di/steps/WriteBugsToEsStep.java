/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Mozilla Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2010
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Michael Kurze (michael@thefoundation.de)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package com.mozilla.bugzilla_etl.di.steps;                                                   // snip
                                                                                             // snip
                                                                                             // snip
import org.pentaho.di.core.RowSet;                                                           // snip
import org.pentaho.di.core.exception.KettleException;                                        // snip
import org.pentaho.di.core.exception.KettleStepException;                                    // snip
import org.pentaho.di.trans.step.StepDataInterface;                                          // snip
import org.pentaho.di.trans.step.StepMetaInterface;                                          // snip
import org.pentaho.di.trans.steps.userdefinedjavaclass.TransformClassBase;                   // snip
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClass;                 // snip
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassData;             // snip
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;             // snip
                                                                                             // snip
                                                                                             // snip
// Allow casts for Janino compatibility (no generic collections).                            // snip
@SuppressWarnings("cast")                                                                    // snip
public class WriteBugsToEsStep extends TransformClassBase {                                  // snip
                                                                                             // step
    public WriteBugsToEsStep(UserDefinedJavaClass parent,                                    // snip
                             UserDefinedJavaClassMeta meta,                                  // snip
                             UserDefinedJavaClassData data) throws KettleStepException {     // snip
        super(parent, meta, data);                                                           // snip
    }                                                                                        // snip

    /**
     * source: {@link com.mozilla.bugzilla_etl.di.steps.WriteBugsToEsStep}
     *
     * Input step(s):
     *    * 0: A stream of bugs (normalized)
     * Output step(s):
     *    (none)
     */
    private com.mozilla.bugzilla_etl.di.BugSource source;
    private com.mozilla.bugzilla_etl.es.BugDestination destination;

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        if (first) {
            first = false;
            RowSet input = (RowSet)this.getInputRowSets().get(0);
            source = new com.mozilla.bugzilla_etl.di.BugSource(this, input);
            final String esNodes = getParameter("T_ES_NODES");
            destination = new com.mozilla.bugzilla_etl.es.BugDestination(System.out,
                                                                         esNodes);
        }
        try {
            if (!source.hasMore()) {
                setOutputDone();
                destination.flush();
                return false;
            }
            destination.send(source.receive());
            incrementLinesWritten();
        }
        catch (Exception error) {
            error.printStackTrace(System.out);
            System.out.format("Repository Error (%s) (see stack trace above),",
                              new Object[] { error.getClass().getSimpleName() });
            throw new RuntimeException(error);
        }
        return true;
    }

}                                                                                            // snip
