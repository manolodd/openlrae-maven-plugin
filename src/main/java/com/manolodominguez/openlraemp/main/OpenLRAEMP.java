/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.openlraemp.main;

import com.manolodominguez.openlraemp.cli.OpenLRAEMPCLIHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a set of options that can be called from CLI by an
 * user, using OpenLRAE as an executable application instead of as a library.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 */
public class OpenLRAEMP {

    private static final Logger logger = LoggerFactory.getLogger(OpenLRAEMP.class);

    /**
     * This is the main method, that converts OpenLRAE in an executable
     * application in addition to be a java library. It reads CLI options and
     * perform some actions.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "-e":
                    new OpenLRAEMPCLIHandler().runExample();
                    break;
                default:
                    new OpenLRAEMPCLIHandler().showOptions();
                    break;
            }
        } else {
            if (args.length == 2) {
                switch (args[0]) {
                    case "-a":
                        new OpenLRAEMPCLIHandler().runAnalysis(args[1]);
                        break;
                    default:
                        new OpenLRAEMPCLIHandler().showOptions();
                        break;
                }
            } else {
                new OpenLRAEMPCLIHandler().showOptions();
            }
        }
    }
}
