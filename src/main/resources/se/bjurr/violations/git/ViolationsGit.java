package se.bjurr.violations.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public final class ViolationsGit {

	private ViolationsGit() {
	}

	public static String diff(final File repo, final String from, final String to) throws Exception {
		final StringBuilder diffText = new StringBuilder();
		Git git = null;
		Repository repository = null;
		try {
			repository = getGitRepo(repo);
			final ObjectId fromObjectId = getObjectId(repository, from);
			final ObjectId toObjectId = getObjectId(repository, to);

			git = new Git(repository);
			final AbstractTreeIterator oldTree = getTree(repository, fromObjectId);
			final AbstractTreeIterator newTree = getTree(repository, toObjectId);
			final List<DiffEntry> diffs = git.diff().setOldTree(oldTree).setNewTree(newTree).call();

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (final DiffFormatter df = new DiffFormatter(out)) {
				df.setRepository(repository);
				for (final DiffEntry diff : diffs) {
					df.setContext(0);
					df.format(diff);
					final RawText r = new RawText(out.toByteArray());
					r.getLineDelimiter();
					diffText.append(out.toString("UTF-8"));
					out.reset();
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Could not use GIT repo in " + repo.getAbsolutePath(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
			if (git != null) {
				git.close();
			}
		}
		return diffText.toString();
	}

	private static AbstractTreeIterator getTree(final Repository repository, final ObjectId objectId) throws Exception {
		try (RevWalk walk = new RevWalk(repository)) {
			final RevCommit commit = walk.parseCommit(objectId);
			final RevTree tree = walk.parseTree(commit.getTree().getId());

			final CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			return treeParser;
		}
	}

	private static ObjectId getObjectId(final Repository repository, final String revstr) throws Exception {
		final ObjectId resolved = repository.resolve(revstr);
		if (resolved == null) {
			throw new RuntimeException("Could not resolve \"" + revstr + "\"");
		}
		return resolved;
	}

	private static Repository getGitRepo(final File repo) throws IOException {
		File repoFile = new File(repo.getAbsolutePath());
		final File gitRepoFile = new File(repo.getAbsolutePath() + "/.git");
		if (gitRepoFile.exists()) {
			repoFile = gitRepoFile;
		}
		final FileRepositoryBuilder builder = new FileRepositoryBuilder() //
				.findGitDir(repoFile) //
				.readEnvironment();
		if (builder.getGitDir() == null) {
			throw new RuntimeException("Did not find a GIT repo in " + repo.getAbsolutePath());
		}
		return builder.build();
	}
}
