// Generated code from Butter Knife. Do not modify!
package cheesewheel.cheesewheel;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SignUp$$ViewInjector<T extends cheesewheel.cheesewheel.SignUp> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492986, "field '_nameText'");
    target._nameText = finder.castView(view, 2131492986, "field '_nameText'");
    view = finder.findRequiredView(source, 2131493009, "field '_emailText'");
    target._emailText = finder.castView(view, 2131493009, "field '_emailText'");
    view = finder.findRequiredView(source, 2131492987, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131492987, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131493010, "field '_signupButton'");
    target._signupButton = finder.castView(view, 2131493010, "field '_signupButton'");
    view = finder.findRequiredView(source, 2131493011, "field '_loginLink'");
    target._loginLink = finder.castView(view, 2131493011, "field '_loginLink'");
    view = finder.findRequiredView(source, 2131492990, "field '_wheelLink'");
    target._wheelLink = finder.castView(view, 2131492990, "field '_wheelLink'");
  }

  @Override public void reset(T target) {
    target._nameText = null;
    target._emailText = null;
    target._passwordText = null;
    target._signupButton = null;
    target._loginLink = null;
    target._wheelLink = null;
  }
}
