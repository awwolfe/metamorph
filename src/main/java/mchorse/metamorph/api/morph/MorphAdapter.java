package mchorse.metamorph.api.morph;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mchorse.metamorph.api.IAbility;

/**
 * Morph adapter
 * 
 * This adapter is responsible for injecting abilities, attacks or actions from 
 * {@link MorphManager} into the create {@link Morph}.
 */
public class MorphAdapter implements JsonDeserializer<Morph>
{
    @Override
    public Morph deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        Morph morph = new Morph();
        MorphManager manager = MorphManager.INSTANCE;
        List<IAbility> abilities = new ArrayList<IAbility>();

        if (object.has("health"))
        {
            morph.health = object.get("health").getAsInt();
        }

        if (object.has("abilities"))
        {
            for (JsonElement ability : object.get("abilities").getAsJsonArray())
            {
                abilities.add(manager.abilities.get(ability.getAsString()));
            }

            morph.abilities = abilities.toArray(new IAbility[abilities.size()]);
        }

        if (object.has("action"))
        {
            morph.action = manager.actions.get(object.get("action").getAsString());
        }

        if (object.has("attack"))
        {
            morph.attack = manager.attacks.get(object.get("attack").getAsString());
        }

        return morph;
    }
}