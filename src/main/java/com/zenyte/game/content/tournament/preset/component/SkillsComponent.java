package com.zenyte.game.content.tournament.preset.component;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Tommeh | 25/05/2019 | 17:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public class SkillsComponent {

    private Map<Integer, Integer> skills;

    public SkillsComponent(final Map<Integer, Integer> skills) {
        this.skills = skills;
    }

    public static class SkillsComponentBuilder {

        private Map<Integer, Integer> skills;

        public SkillsComponentBuilder() {
            skills = new TreeMap<>();
            for (int skill = 0; skill < 23; skill++) {
                skills.put(skill, 1);
            }
        }

        public SkillsComponent.SkillsComponentBuilder set(final int skill, final int level) {
            skills.put(skill, level);
            return this;
        }

        public SkillsComponent build() {
            return new SkillsComponent(skills);
        }

    }
}