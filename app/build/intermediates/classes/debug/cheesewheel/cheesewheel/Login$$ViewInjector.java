// Generated code from Butter Knife. Do not modify!
package cheesewheel.cheesewheel;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class Login$$ViewInjector<T extends cheesewheel.cheesewheel.Login> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492984, "field '_emailText'");
    target._emailText = finder.castView(view, 2131492984, "field '_emailText'");
    view = finder.findRequiredView(source, 2131492985, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131492985, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131492986, "field '_loginButton'");
    target._loginButton = finder.castView(view, 2131492986, "field '_loginButton'");
    view = finder.findRequiredView(source, 2131492987, "field '_signupLink'");
    target._signupLink = finder.castView(view, 2131492987, "field '_signupLink'");
    view = finder.findRequiredView(source, 2131492988, "field '_wheelLink'");
    target._wheelLink = finder.castView(view, 2131492988, "field '_wheelLink'");
  }

  @Override public void reset(T target) {
    target._emailText = null;
    target._passwordText = null;
    target._loginButton = null;
    target._signupLink = null;
    target._wheelLink = null;
  }
}
