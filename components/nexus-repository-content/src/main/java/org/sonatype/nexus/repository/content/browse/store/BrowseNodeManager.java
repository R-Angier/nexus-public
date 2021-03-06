/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.repository.content.browse.store;

import java.util.List;
import java.util.function.Consumer;

import org.sonatype.nexus.repository.browse.node.BrowsePath;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages browse nodes for a specific repository.
 *
 * @since 3.next
 */
public class BrowseNodeManager
{
  private final BrowseNodeStore<BrowseNodeDAO> browseNodeStore;

  private final int repositoryId;

  public BrowseNodeManager(final BrowseNodeStore<BrowseNodeDAO> browseNodeStore, final int repositoryId) {
    this.browseNodeStore = checkNotNull(browseNodeStore);
    this.repositoryId = repositoryId;
  }

  /**
   * Deletes all browse nodes associated with the repository.
   */
  public void deleteBrowseNodes() {
    browseNodeStore.deleteBrowseNodes(repositoryId);
  }

  /**
   * Creates browse nodes for the path, applying a final step to the last node.
   */
  public void createBrowseNodes(final List<BrowsePath> paths, final Consumer<BrowseNodeData> finalStep) {
    Integer parentId = null;
    for (int i = 0; i < paths.size(); i++) {
      BrowseNodeData node = new BrowseNodeData();
      node.setRepositoryId(repositoryId);
      node.setRequestPath(paths.get(i).getRequestPath());
      node.setDisplayName(paths.get(i).getDisplayName());
      if (parentId != null) {
        node.setParentId(parentId);
      }
      if (i == paths.size() - 1) {
        finalStep.accept(node);
      }
      browseNodeStore.mergeBrowseNode(node);
      parentId = node.nodeId;
    }
  }
}
