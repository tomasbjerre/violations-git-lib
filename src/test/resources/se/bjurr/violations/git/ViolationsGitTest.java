package se.bjurr.violations.git;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViolationsGitTest {
	private static Logger LOG = LoggerFactory.getLogger(ViolationsGitTest.class);

	@Test
	public void test() throws Exception {
		final File repo = new File(".");
		final String from = "HEAD";
		final String to = "HEAD";
		final String diff = ViolationsGit.diff(repo, from, to);

		LOG.info(diff);
	}

}
