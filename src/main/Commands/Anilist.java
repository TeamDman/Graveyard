package main.Commands;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import com.google.gson.Gson;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArguments;
import main.Commands.obj.IInvocable;
import main.Commands.obj.RegisterCommand;
import main.Handlers.CommandHandler;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import sx.blah.discord.util.EmbedBuilder;

import java.io.InputStream;
import java.util.List;

@RegisterCommand
public class Anilist extends Command implements IInvocable<Anilist.Options> {

	public Anilist() {
		super("Anilist", "anilist", Options.class, null);
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		if (args.options.name.isEmpty())
			throw new CommandHandler.MissingOptionException("-u parameter required");
		HttpClient          client = HttpClients.createDefault();
		HttpPost            post   = new HttpPost("https://graphql.anilist.co");
		List<NameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("query",
				"query ($id: Int) { \n" +
						"  User(id: $id, search: \"" + args.options.name + "\") {\n" +
						"    id\n" +
						"    name\n" +
						"    siteUrl\n" +
						"    moderatorStatus\n" +
						"    avatar {\n" +
						"      large\n" +
						"    }\n" +
						"    stats {\n" +
						"      watchedTime\n" +
						"      chaptersRead\n" +
						"      animeListScores {\n" +
						"        meanScore\n" +
						"      }\n" +
						"      mangaListScores {\n" +
						"        meanScore\n" +
						"      }\n" +
						"    }\n" +
						"  }\n" +
						"}"));
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse resp   = client.execute(post);
		HttpEntity   entity = resp.getEntity();
		if (entity != null) {
			try (InputStream in = entity.getContent()) {
				String strResp = IOUtils.toString(in, "UTF-8");
				Gson        gson = new Gson();
				AnilistInfo info = gson.fromJson(strResp, AnilistInfo.class);
				args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle(args.options.name + "'s AniList Info")
						.withImage(info.data.User.avatar.large)
						.build()
				);
				//				args.message.getChannel().sendMessage(IOUtils.toString(in, "UTF-8"));
			}
		}
	}

	public static class Options extends OptionsDefault {
		@Option(
				name = "Username",
				abbrev = 'u',
				help = "The username to display info for",
				defaultValue = ""
		)
		public String name;
	}
	//
	//	class InstanceCreatorForAnilistInfo implements InstanceCreator<AnilistInfo> {
	//		priv
	//	}
	private class AnilistInfo {
		data data;
	}

	private class data {
		User User;
	}

	private class User {
		int id;
		String name;
		String siteUrl;
		String moderatorStatus;
		avatar avatar;
		stats stats;
	}

	private class avatar {
		String large;
	}
	private class stats {
		int watchedTime;
		int chaptersRead;
		int[] animeListScores;
		int[] mangaListScores;
	}
}
