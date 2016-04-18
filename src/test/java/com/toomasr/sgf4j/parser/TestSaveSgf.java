package com.toomasr.sgf4j.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import com.toomasr.sgf4j.Sgf;
import com.toomasr.sgf4j.SgfParseException;

public class TestSaveSgf {
  @Test
  public void testSimple() throws Exception {
    Path sgfPath = Paths.get("./src/main/resources/simple-12-move-game.sgf");
    verifyGame(sgfPath);
  }

  @Test
  public void testProblematic() throws Exception {
    Path sgfPath = Paths.get("./src/test/resources/problematic-001.sgf");
    verifyGame(sgfPath);

    sgfPath = Paths.get("./src/test/resources/problematic-002.sgf");
    verifyGame(sgfPath);
  }

  @Test
  public void testAllGamesFromBadukMoviesArchive() {
    Path path = Paths.get("src/test/resources/badukmovies-pro-collection.zip");
    testAllGamesInZipArchive(path);
  }

  @Test
  public void testAllGamesFromAebArchive() {
    Path path = Paths.get("src/test/resources/games-aeb-cwi-nl.zip");
    testAllGamesInZipArchive(path);
  }

  private void testAllGamesInZipArchive(Path path) {
    ZipUtil.iterate(path.toFile(), new ZipEntryCallback() {

      @Override
      public void process(InputStream in, ZipEntry zipEntry) throws IOException {
        if (zipEntry.toString().endsWith("sgf")) {

          try {
            Game game = Sgf.createFromInputStream(in);
            verifyGame(game);
          }
          catch (SgfParseException e) {
            System.out.println("Problem with " + zipEntry.getName());
            e.printStackTrace();
            Assert.fail();
          }
          catch (AssertionError e) {
            System.out.println("Problem with " + zipEntry.getName());
            throw e;
          }
        }
      }
    });
    Assert.assertTrue(true);
  }

  /*
   * Create a Game object. Save to file. Create a new game object.
   * Compare the first and last and see if our writing is working.
   */
  private void verifyGame(Path sgfPath) throws Exception {
    Game game = Sgf.createFromPath(sgfPath);
    verifyGame(game);
  }

  private void verifyGame(Game game) {
    File file = null;
    try {
      file = File.createTempFile("sgf4j-test-", ".sgf");
      file.deleteOnExit();

      game.saveToFile(file.toPath());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    Game reReadGame = Sgf.createFromPath(file.toPath());

    Assert.assertTrue("Problem with game " + file.getName(), game.isSameGame(reReadGame));
  }
}
