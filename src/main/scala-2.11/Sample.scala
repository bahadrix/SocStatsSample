/**
 * Created by bahadir on 18/01/16.
 * Row pattern: Div,Date,HomeTeam,AwayTeam,FTHG,FTAG,FTR,HTHG,HTAG,HTR,B365H,B365D,B365A,GBH,GBD,GBA,IWH,IWD,IWA,LBH,LBD,LBA,SBH,SBD,SBA,WHH,WHD,WHA,GB>2.5,GB<2.5,GBAHH,GBAHA,GBAH
 */

object Sample extends App{

  val rows = Global.sparkContext.textFile("data/turkey/*.csv")
    .map(line => StatsRow(line.split(",")))


  //getTeams()

  val res = getTeamScores()

  Global.sparkContext.stop()

  Thread.sleep(1000)

  println()

  res.foreach(println)


  def getTeamScores(): Array[String] = {

    val scores =rows
      .map(row => {

        val homeTeam = row.get("HomeTeam").get
        val awayTeam = row.get("AwayTeam").get

        val homeGoals = toInt(row.get("FTHG").get) // Full time home goals
        val awayGoals = toInt(row.get("FTAG").get) // Full time away goals

        Array((homeTeam, homeGoals), (awayTeam, awayGoals))

      })
      .reduce(_ ++ _)

    Global.sparkContext.parallelize(scores)
      .reduceByKey(_+_)
      .map(r => s"${r._1}\t:${r._2} goals")
      .collect()





  }

  def getTeams(): Array[String] = {

    val teams = rows
      .map(row => {
        Array(row.get("HomeTeam").get, row.get("AwayTeam").get)

    })
      .reduce(_ ++ _)

    Global.sparkContext.parallelize(teams)
      .distinct()
      .collect();


  }

  def toInt(s: String): Int = {
    try {
      s.toInt
    } catch {
      case e: Exception => 0
    }
  }
}

case class StatsRow(rowData:Array[String]) {

  val fieldNames = Array("Div","Date","HomeTeam","AwayTeam","FTHG","FTAG","FTR","HTHG","HTAG","HTR","B365H","B365D","B365A","GBH","GBD","GBA","IWH","IWD","IWA","LBH","LBD","LBA","SBH","SBD","SBA","WHH","WHD","WHA","GB>2.5","GB<2.5","GBAHH","GBAHA","GBAH")

  // Map fieldnames to indices
  val fieldMap = Map[String, Int]() ++ fieldNames.zipWithIndex


  /**
   * Get corresponding value by given field key.
   * @param fieldName Possible values are: Div,Date,HomeTeam,AwayTeam,FTHG,FTAG,FTR,HTHG,HTAG,HTR,B365H,B365D,B365A,GBH,GBD,GBA,IWH,IWD,IWA,LBH,LBD,LBA,SBH,SBD,SBA,WHH,WHD,WHA,GB>2.5,GB<2.5,GBAHH,GBAHA,GBAH
   * @return Field value
   */
  def get(fieldName:String): Option[String] = {

    val idx = fieldMap.getOrElse(fieldName, -1)
    if (idx > 0)
      Some(rowData(idx))
    else
      None

  }



}