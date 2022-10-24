//InstantiateConfiguration.java
//obsolete, not used anywhere anymore

/*
--------------------------------------------------------------------------
Copyright 2021 Adventium Enterprises, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--------------------------------------------------------------------------
*/


package com.adventium.design.exploration.actions;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.adventium.design.exploration.Say;

/**
 * @author brl
 *
 */
public class InstantiateConfiguration implements
    IWorkbenchWindowActionDelegate, IObjectActionDelegate
{

/* (non-Javadoc)
 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
 */
public void run(IAction action)
  {
  final Job job = new InstantiateJob();
//  final Job job = new InstantiateJob(currentSelection);
  job.setRule(ResourcesPlugin.getWorkspace().getRoot());
  job.setUser(true); // important!
  job.schedule();
  }

private final class InstantiateJob extends WorkspaceJob 
{
//private final Set selection;

public InstantiateJob() 
{
    super("Instantiate configuration");
//    this.selection = selection;
}

//public InstantiateJob(final Set selection) 
//{
//    super("Instantiate configuration");
//    this.selection = selection;
//}

@Override
public IStatus runInWorkspace(final IProgressMonitor monitor) {
    monitor.beginTask("Instantiate selected configuration", IProgressMonitor.UNKNOWN);
    try {
    Say.it("Instantiate selected configuration");
    //invoke instantiation here
    
    
//        if (!selection.isEmpty()) {
//            Iterator selit = selection.iterator();
//            for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//                Object obj = iterator.next();
//                if (obj instanceof IResource) {
//                    InstantiateModel.rebuildInstanceModelFile((IResource) obj);
//                }
//            }
//        } else {
//            InstantiateModel.rebuildAllInstanceModelFiles();
//        }
    } catch (Exception e) {
        System.err.println("Instantiate Configuration: Error while instantiating selected configuration: " + e.getMessage());

    }
    monitor.done();
    return Status.OK_STATUS;
}
}

public void selectionChanged(IAction arg0, ISelection arg1) {
	// TODO Auto-generated method stub
	
}

public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
	// TODO Auto-generated method stub
	
}

public void dispose() {
	// TODO Auto-generated method stub
	
}

public void init(IWorkbenchWindow arg0) {
	// TODO Auto-generated method stub
	
}



}
