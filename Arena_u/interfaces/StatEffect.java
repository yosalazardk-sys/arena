package interfaces;

import Model.ModelWarrior;

@FunctionalInterface
public interface StatEffect {

    void apply(ModelWarrior warrior);
}

