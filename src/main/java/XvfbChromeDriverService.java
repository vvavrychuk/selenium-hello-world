import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.io.IOException;

class XvfbChromeDriverService extends ChromeDriverService {
  XvfbChromeDriverService(File executable, int port, ImmutableList<String> args, ImmutableMap<String, String> environment) throws IOException {
    super(executable, port, args, environment);
  }

  public static ChromeDriverService createDefaultService() {
    return new Builder().usingAnyFreePort().build();
  }

  public static class Builder extends ChromeDriverService.Builder {
    @Override
    protected File findDefaultExecutable() {
      return findExecutable("xvfb-run", "dummy", null, null);
    }

    @Override
    protected ImmutableList<String> createArgs() {
      try {
        return ImmutableList.<String>builder()
          .add(super.findDefaultExecutable().getCanonicalPath())
          .addAll(super.createArgs())
          .build();
      } catch (IOException e) {
        throw new WebDriverException(e);
      }
    }

    @Override
    protected ChromeDriverService createDriverService(File exe, int port,
                                                      ImmutableList<String> args,
                                                      ImmutableMap<String, String> environment) {
      try {
        return new XvfbChromeDriverService(exe, port, args, environment);
      } catch (IOException e) {
        throw new WebDriverException(e);
      }
    }
  }
}
