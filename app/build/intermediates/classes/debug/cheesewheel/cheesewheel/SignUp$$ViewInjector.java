// Generated code from Butter Knife. Do not modify!
package cheesewheel.cheesewheel;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SignUp$$ViewInjector<T extends cheesewheel.cheesewheel.SignUp> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492988, "field '_nameText'");
    target._nameText = finder.castView(view, 2131492988, "field '_nameText'");
    view = finder.findRequiredView(source, 2131492971, "field '_emailText'");
    target._emailText = finder.castView(view, 2131492971, "field '_emailText'");
    view = finder.findRequiredView(source, 2131492972, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131492972, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131492989, "field '_signupButton'");
    target._signupButton = finder.castView(view, 2131492989, "field '_signupButton'");
    view = finder.findRequiredView(source, 2131492990, "field '_loginLink'");
    target._loginLink = finder.castView(view, 2131492990, "field '_loginLink'");
  }

  @Override public void reset(T target) {
    target._nameText = null;
    target._emailText = null;
    target._passwordText = null;
    target._signupButton = null;
    target._loginLink = null;
  }
}
